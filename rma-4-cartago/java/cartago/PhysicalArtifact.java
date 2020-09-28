package cartago;

import br.pro.turing.javino.Javino;
import br.pro.turing.rma.core.model.Action;
import br.pro.turing.rma.core.model.Data;
import br.pro.turing.rma.core.model.Device;
import br.pro.turing.rma.core.model.Resource;
import br.pro.turing.rma.devicelayer.IoTObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class PhysicalArtifact extends Artifact {

    private Javino javino;

    private String port;

    private int attemptsAfterFailure;

    private int waitTimeout;

    private boolean javinoIsBusy = false;

    private IoTObject ioTObject;

    public PhysicalArtifact() {
        super();
        this.javino = new Javino();
        this.port = definePort();
        if (this.port == null) {
            this.port = "";
        }
        this.attemptsAfterFailure = defineAttemptsAfterFailure();
        this.waitTimeout = defineWaitTimeout();
    }

    protected abstract String definePort();

    protected abstract int defineAttemptsAfterFailure();

    protected abstract int defineWaitTimeout();

    public abstract void onIoTAction(String command);

    public void enableIoT(String deviceConfigFolder, String gatewayIP, int gatewayPort) {
        try {
            String configFilePath = new File(deviceConfigFolder).getPath() + File.separator
                    + "deviceConfiguration.json";
            Device artifactDevice = IoTObject.buildDeviceByConfigFile(configFilePath);
            this.ioTObject = new IoTObject(artifactDevice) {
                @Override
                protected void onAction(Action action) {
                    PhysicalArtifact.this.onIoTAction(action.getCommand());
                }

                @Override
                protected ArrayList<Data> buildDataBuffer() {
                    ArrayList<Data> dataList = new ArrayList<Data>();

                    // Removendo o último SPLIT_VALUE de cada recurso.
                    final LocalDateTime now = LocalDateTime.now();
                    for (Resource r : this.getDevice().getResourceList()) {
                        if (getObsProperty(r.getResourceName()) != null) {
                            String value = getObsProperty(r.getResourceName()).stringValue();
                            dataList.add(new Data(now, this.getDevice().getDeviceName(), r.getResourceName(), value));
                        }
                    }

                    return dataList;
                }
            };
            this.ioTObject.connect(gatewayIP, gatewayPort);
            log("Connected in RML as an IoT device");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OPERATION
    public void percepts() {
        while (javinoIsBusy) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        javinoIsBusy = true;
        boolean hasData = this.javino.requestData(this.port, "getPercepts");
        String[] results = hasData ? this.javino.getData().split(";") : new String[]{};
        javinoIsBusy = false;

        for (String result : results) {
            String[] datas = result.replace(")", "").split("\\(");
            ObsProperty prop = getObsProperty(datas[0]);
            if (prop != null) {
                prop.updateValue(datas[1]);
            } else {
                defineObsProperty(datas[0], datas[1]);
            }
        }
    }

    @OPERATION
    public void act(String message) {
        while (javinoIsBusy) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        javinoIsBusy = true;
        int failureCount = 0;
        while (!this.javino.sendCommand(this.port, message) && failureCount < this.attemptsAfterFailure) {
            try {
                Thread.sleep(this.waitTimeout);
                failureCount++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        javinoIsBusy = false;
    }
}
