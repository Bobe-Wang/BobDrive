package ai.flow.app;

import ai.flow.app.helpers.Utils;
import ai.flow.common.transformations.Camera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import ai.flow.app.CalibrationScreens.CalibrationInfo;
import ai.flow.common.ParamsInterface;
import ai.flow.common.SystemUtils;


public class SettingsScreen extends ScreenAdapter {

    FlowUI appContext;
    ParamsInterface params = ParamsInterface.getInstance();
    Stage stage;
    TextButton buttonDevice, buttonCalibrate, buttonWideCalibrate, buttonCalibrateExtrinsic,
            buttonTraining, buttonPowerOff, buttonReboot, buttonSoftware,
            buttonUninstall, buttonToggle, buttonCheckUpdate;
    ImageButton closeButton;
    CheckBox recordRoadCamToggle, FPToggle, F3Toggle, LDWToggle, RHDToggle, MetricToggle,
            recordDriverCamToggle, lanelessToggle, disengageAccToggle;

    SpriteBatch batch;
    Table rootTable, settingTable, scrollTable, currentSettingTable;
    Texture lineTex = Utils.getLineTexture(700, 1, Color.WHITE);
    ScrollPane scrollPane;

    public void addKeyValueTable(Table table, String key, String value, boolean addLine) {
        table.add(new Label(key, appContext.skin, "default-font", "white")).left().pad(30);
        table.add(new Label(value, appContext.skin, "default-font", "white")).right().pad(30);
        if (addLine) {
            table.row();
            table.add(new Image(lineTex)).colspan(2).pad(10, 0, 10, 0).height(1);
            table.row();
        }
    }

    public void addKeyValueTable(Table table, String key, Button value, boolean addLine) {
        table.add(new Label(key, appContext.skin, "default-font", "white")).left().pad(30);
        table.add(value).right().pad(30);
        if (addLine) {
            table.row();
            table.add(new Image(lineTex)).colspan(2).pad(10, 0, 10, 0).height(1);
            table.row();
        }
        else
            table.row();
    }

    public void fillDeviceSettings(){
        currentSettingTable.clear();
        String dongleID = params.exists("DongleId") ? params.getString("DongleId") : "N/A";
        addKeyValueTable(currentSettingTable, "Dongle ID", dongleID, true);
        String deviceManufacturer = params.exists("DeviceManufacturer") ? params.getString("DeviceManufacturer") : "";
        addKeyValueTable(currentSettingTable, "Device Manufacturer", deviceManufacturer, true);
        String deviceModel = params.exists("DeviceModel") ? params.getString("DeviceModel") : "";
        addKeyValueTable(currentSettingTable, "Device Name", deviceModel, true);
        addKeyValueTable(currentSettingTable, "Reset Intrinsic Calibration", buttonCalibrate, true);
        if (appContext.isF3 & !params.existsAndCompare("WideCameraOnly", true))
            addKeyValueTable(currentSettingTable, "Reset Wide Intrinsic Calibration", buttonWideCalibrate, true);
        addKeyValueTable(currentSettingTable, "Reset Extrinsic Calibration", buttonCalibrateExtrinsic, true);
        addKeyValueTable(currentSettingTable, "Review Training Guide", buttonTraining, true);
        currentSettingTable.add(buttonReboot).pad(20);
        currentSettingTable.add(buttonPowerOff).pad(20);
    }

    public void fillSoftwareSettings(){
        currentSettingTable.clear();
        String version = params.exists("Version") ? "flowpilot v" + params.getString("Version") : "";
        addKeyValueTable(currentSettingTable, "Version", version, true);

        addKeyValueTable(currentSettingTable, "Last Updated", "", true);
        addKeyValueTable(currentSettingTable, "Check For Update", buttonCheckUpdate, true);

        String branch = params.exists("GitBranch") ? params.getString("GitBranch") : "";
        addKeyValueTable(currentSettingTable, "Git Branch", branch, true);

        String commit = params.exists("GitCommit") ? params.getString("GitCommit").substring(0, 10) : "";
        addKeyValueTable(currentSettingTable, "Git Commit", commit, true);

        addKeyValueTable(currentSettingTable, "Device Type", SystemUtils.getPlatform(), true);
        addKeyValueTable(currentSettingTable, "Uninstall FlowPilot", buttonUninstall, false);
    }

    public void fillToggleSettings(){
        currentSettingTable.clear();
        addKeyValueTable(currentSettingTable, "Enable FlowPilot", FPToggle, true);
        addKeyValueTable(currentSettingTable, "Enable F3", F3Toggle, true);
        addKeyValueTable(currentSettingTable, "Enable Lane Departure Warnings", LDWToggle, true);
        addKeyValueTable(currentSettingTable, "Enable Right Hand Driving", RHDToggle, true);
        addKeyValueTable(currentSettingTable, "Use Metric System", MetricToggle, true);
        addKeyValueTable(currentSettingTable, "Record & Upload Road Camera", recordRoadCamToggle, true);
        addKeyValueTable(currentSettingTable, "Record & Upload Driver Camera", recordDriverCamToggle, true);
        addKeyValueTable(currentSettingTable, "Disable Use of LaneLines (alpha)", lanelessToggle, true);
        addKeyValueTable(currentSettingTable, "Disengage on Accelerator Pedal", disengageAccToggle, false);
    }

    public SettingsScreen(FlowUI appContext) {
        this.appContext = appContext;

        stage = new Stage(new FitViewport(1280, 720));
        batch = new SpriteBatch();

        rootTable = new Table();
        rootTable.setFillParent(true);

        settingTable = new Table();
        scrollTable = new Table();
        currentSettingTable = new Table();

        rootTable.add(settingTable);
        rootTable.add(scrollTable).pad(10).padLeft(20);

        scrollPane = new ScrollPane(currentSettingTable);
        scrollPane.setSmoothScrolling(true);
        scrollTable.add(scrollPane);
        scrollTable.setBackground(Utils.createRoundedRectangle(800, 700, 20, new Color(0.18f, 0.18f, 0.18f, 0.8f)));

        closeButton = Utils.getImageButton("selfdrive/assets/icons/icon_close.png");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                appContext.setScreen(appContext.onRoadScreen);
            }
        });
        closeButton.setColor(1, 1, 1, 0.7f);
        settingTable.add(closeButton).align(Align.left).padLeft(100).padBottom(70).size(70);
        settingTable.row();

        buttonDevice = new TextButton("Device", appContext.skin, "no-bg-bold");
        buttonDevice.setChecked(true);
        buttonDevice.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fillDeviceSettings();
            }
        });
        settingTable.add(buttonDevice).pad(10).align(Align.right);
        settingTable.row();

        buttonSoftware = new TextButton("Software", appContext.skin, "no-bg-bold");
        buttonSoftware.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fillSoftwareSettings();
            }
        });
        settingTable.add(buttonSoftware).pad(10).align(Align.right);
        settingTable.row();

        buttonToggle = new TextButton("Toggles", appContext.skin, "no-bg-bold");
        buttonToggle.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fillToggleSettings();
            }
        });
        settingTable.add(buttonToggle).pad(10).align(Align.right);
        settingTable.row();

        buttonCalibrate = new TextButton("RESET", appContext.skin);
        buttonCalibrate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                appContext.setScreen(new CalibrationInfo(appContext, Camera.CAMERA_TYPE_ROAD,true));
            }
        });

        buttonWideCalibrate = new TextButton("RESET", appContext.skin);
        buttonWideCalibrate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                appContext.setScreen(new CalibrationInfo(appContext, Camera.CAMERA_TYPE_WIDE,true));
            }
        });

        buttonCalibrateExtrinsic = new TextButton("RESET", appContext.skin);
        buttonCalibrateExtrinsic.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                params.putBool("ResetExtrinsicCalibration", true);
            }
        });

        buttonTraining = new TextButton("REVIEW", appContext.skin);
        buttonTraining.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                appContext.setScreen(new TrainingScreen(appContext));
            }
        });

        buttonCheckUpdate = new TextButton("CHECK", appContext.skin);
        buttonCheckUpdate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });

        buttonReboot = new TextButton("Reboot", appContext.skin);
        buttonReboot.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });

        buttonPowerOff = new TextButton("Power Off", appContext.skin, "critical");
        buttonPowerOff.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });

        buttonUninstall = new TextButton("UNINSTALL", appContext.skin, "critical");
        buttonUninstall.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });

        FPToggle = new CheckBox("", appContext.skin);
        FPToggle.setChecked(params.exists("FlowpilotEnabledToggle") && params.getBool("FlowpilotEnabledToggle"));
        FPToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                params.putBool("FlowpilotEnabledToggle", FPToggle.isChecked());
            }
        });

        F3Toggle = new CheckBox("", appContext.skin);
        F3Toggle.setChecked(params.exists("F3") && params.getBool("F3"));
        F3Toggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                params.putBool("F3", F3Toggle.isChecked());
            }
        });

        LDWToggle = new CheckBox("", appContext.skin);
        LDWToggle.setChecked(params.exists("IsLdwEnabled") && params.getBool("IsLdwEnabled"));
        LDWToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                params.putBool("IsLdwEnabled", LDWToggle.isChecked());
            }
        });

        RHDToggle = new CheckBox("", appContext.skin);
        RHDToggle.setChecked(params.exists("IsRHD") && params.getBool("IsRHD"));
        RHDToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                params.putBool("IsRHD", RHDToggle.isChecked());
            }
        });

        MetricToggle = new CheckBox("", appContext.skin);
        MetricToggle.setChecked(params.exists("IsMetric") && params.getBool("IsMetric"));
        MetricToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                params.putBool("IsMetric", MetricToggle.isChecked());
                appContext.onRoadScreen.isMetric = MetricToggle.isChecked();
            }
        });

        recordRoadCamToggle = new CheckBox("", appContext.skin);
        recordRoadCamToggle.setChecked(params.exists("RecordRoad") && params.getBool("RecordRoad"));
        recordRoadCamToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                appContext.sensors.get("roadCamera").record(recordRoadCamToggle.isChecked());
            }
        });

        recordDriverCamToggle = new CheckBox("", appContext.skin);
        recordDriverCamToggle.setChecked(params.exists("RecordFront") && params.getBool("RecordFront"));
        recordDriverCamToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
            }
        });

        lanelessToggle = new CheckBox("", appContext.skin);
        lanelessToggle.setChecked(params.exists("EndToEndToggle") && params.getBool("EndToEndToggle"));
        lanelessToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                params.putBool("EndToEndToggle", lanelessToggle.isChecked());
            }
        });

        disengageAccToggle = new CheckBox("", appContext.skin);
        disengageAccToggle.setChecked(params.exists("DisengageOnAccelerator") && params.getBool("DisengageOnAccelerator"));
        disengageAccToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                params.putBool("DisengageOnAccelerator", disengageAccToggle.isChecked());
            }
        });

        fillDeviceSettings();

        ButtonGroup buttonGroup = new ButtonGroup(buttonDevice, buttonSoftware, buttonToggle);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setUncheckLast(true);

        stage.addActor(rootTable);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.0f, .0f, .0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
        stage.dispose();
    }
}
