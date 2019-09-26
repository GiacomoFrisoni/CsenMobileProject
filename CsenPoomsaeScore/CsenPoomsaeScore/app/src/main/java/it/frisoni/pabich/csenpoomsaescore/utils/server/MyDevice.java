package it.frisoni.pabich.csenpoomsaescore.utils.server;

public class MyDevice {
    private String deviceID;
    private MyDeviceStatus status;

    public MyDevice() {
        this.deviceID = null;
        this.status = MyDeviceStatus.NOT_CONNECTED;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public MyDeviceStatus getStatus() {
        return status;
    }

    public void setAsNotConnected() {
        changeDeviceStatus(null, MyDeviceStatus.NOT_CONNECTED);
    }

    public void setAsConnectionPending() {
        changeDeviceStatus(null, MyDeviceStatus.PENDING);
    }

    public void setAsConnected(final String deviceID) {
        changeDeviceStatus(deviceID, MyDeviceStatus.CONNECTED);
    }


    private void changeDeviceStatus(final String deviceID, final MyDeviceStatus status) {
        this.deviceID = deviceID;
        this.status = status;
    }


}
