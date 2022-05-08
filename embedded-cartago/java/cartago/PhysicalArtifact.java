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

    public void enableIoT(String deviceConfigFolder, String gatewayIP, int gatewayPort) {
        try {
            String configFilePath = new File(deviceConfigFolder).getPath() + File.separator
                    + "deviceConfiguration.json";
            Device artifactDevice = IoTObject.buildDeviceByConfigFile(configFilePath);
            this.ioTObject = new IoTObject(artifactDevice) {
                @Override
                protected void onAction(Action action) {
                    PhysicalArtifact.this.act(action.getCommand());
                }

                @Override
                protected ArrayList<Data> buildDataBuffer() {
                    LocalDateTime now = LocalDateTime.now();
                    String[] results = requestData();
                    ArrayList<Data> dataList = new ArrayList<Data>();
                    for (String result : results) {
                        String[] datas = result.replace(")", "").split("\\(");
                        final Resource resource = this.getDevice().getResourceList().stream().filter(
                                resource1 -> resource1.getResourceName().equals(datas[0])).findFirst().orElse(null);
                        if (resource != null) {
                            String value = datas[1];
                            dataList.add(new Data(now, this.getDevice().getDeviceName(), resource.getResourceName(), value));
                            PhysicalArtifact.this.setObsProperties(datas);
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

    private void setObsProperties(String[] datas) {
        ObsProperty prop = getObsProperty(datas[0]);
        if (prop != null) {
            prop.updateValue(datas[1]);
        } else {
            defineObsProperty(datas[0], datas[1]);
        }
    }

    private String[] requestData() {
        while (javinoIsBusy) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        javinoIsBusy = true;
        boolean hasData = this.javino.requestData(this.port, "getPercepts");
        javinoIsBusy = false;
        return hasData ? this.javino.getData().split(";") : new String[]{};
    }


    @OPERATION
    public void percepts() {
        if (this.ioTObject != null) {
            log("The percepts operation does not produce effect because this artifact is an IoT Object");
            return;
        }
        String[] results = requestData();
        for (String result : results) {
            String[] datas = result.replace(")", "").split("\\(");
            setObsProperties(datas);
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
