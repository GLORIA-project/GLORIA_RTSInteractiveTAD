package eu.gloria.tad.rtd;

import eu.gloria.rt.entity.device.ActivityStateFocuser;
import eu.gloria.rt.entity.device.AlarmState;
import eu.gloria.rt.entity.device.BlockState;
import eu.gloria.rt.entity.device.CommunicationState;
import eu.gloria.rt.entity.device.Device;
import eu.gloria.rt.entity.device.DeviceFocuser;
import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.entity.device.MeasureUnit;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rtd.RTDFocuserInterface;
import eu.gloria.tools.log.LogUtil;
import eu.gloria.tools.time.RunTimeCounter;

public class FocuserRTD extends DeviceRTD implements RTDFocuserInterface {
	
	
	private FocuserRTDDriverTADs driver = new FocuserRTDDriverTADs();

	public FocuserRTD(String deviceId) throws RTException {
		super(deviceId);
		
		RunTimeCounter rtc = new RunTimeCounter("FocuserRTD", this.getDeviceId());
		rtc.start();
		
		readConfig();
		
		driver = new FocuserRTDDriverTADs();
		
		rtc.stop();
		rtc.writeLog("Constructor");
		
	}
	
	private void readConfig() throws RTException {
		
		try{
			
			/*DeviceProperty configIndexProp = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "CONFIG_INDEX");
			DeviceProperty ipProp = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "IP");
			DeviceProperty portProp = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "PORT");
			
			String[] names ={"CONFIG_INDEX", "IP", "PORT"};
			String[] values = {configIndexProp.getDefaultValue(), ipProp.getDefaultValue(), portProp.getDefaultValue()};
			LogUtil.info(this, "Dome Config =" + LogUtil.getLog(names, values));
			
			
			configIndex = Integer.parseInt(configIndexProp.getDefaultValue());
			
			domeImpl = new TadDomeSocketImpl(ipProp.getDefaultValue(), Integer.parseInt(portProp.getDefaultValue()));*/
			
		}catch(Exception ex){
			LogUtil.severe(this, "Constructor. Error:" + ex.getMessage());
			//Exception not thrown to get the device in error state
//			if (ex.getMessage() != null && ex.getMessage().indexOf("Not driver found") ==  -1){
//				throw new RTException(ex.getMessage());
//			}
		}
		
		
	}

	

	@Override
	public String focGetCamera() throws RTException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean focIsAbsolute() throws RTException {
		return true;
	}

	@Override
	public double focGetStepSize() throws RTException {
		return -1;
	}

	@Override
	public long focGetMaxIncrement() throws RTException {
		
		try{
			return driver.getMaxIncrement();
		}catch(Exception ex){
			throw new RTException("FocuserRTD.focGetMaxIncrement" + ex.getMessage());
		}
		
	}

	@Override
	public long focGetMaxStep() throws RTException {

		try{
			return driver.getMaxStep();
		}catch(Exception ex){
			throw new RTException("FocuserRTD.focGetMaxStep" + ex.getMessage());
		}
		
	}

	@Override
	public long focGetPosition() throws RTException {

		try{
			return driver.getCurrentPos();
		}catch(Exception ex){
			throw new RTException("FocuserRTD.focGetPosition" + ex.getMessage());
		}
		
	}

	@Override
	public boolean focIsTempCompAvailable() throws RTException {
		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public double focGetTemperature() throws RTException {
		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public void focSetTempComp(boolean trackingMode) throws RTException {
		throw new UnsupportedOpException ("Operation not supported");
		
	}

	@Override
	public void focHalt() throws RTException {
		
		try{
			driver.stop();
		}catch(Exception ex){
			throw new RTException("FocuserRTD.focHalt" + ex.getMessage());
		}
		
	}

	@Override
	public void focMove(long position) throws RTException {

		try{
			driver.move(position);
		}catch(Exception ex){
			throw new RTException("FocuserRTD.focMove" + ex.getMessage());
		}
		
	}

	@Override
	public Device devGetDevice(boolean allProperties) throws RTException {
		
		RunTimeCounter rtc = new RunTimeCounter("FocuserRTD", this.getDeviceId());
		rtc.start();
		
		try{
			
			boolean error = false;
			
			try{
				
				driver.getCurrentPos(); //Forces to initialize the driver if it hasn't be initilized
				
			}catch(Exception ex){
				LogUtil.severe(this, "Error initializing the FocuserDriver. " + ex.getMessage());
				error = true;
			}
			
			DeviceFocuser dev = new DeviceFocuser();
				
			//	Resolve the activity state
			if (error){ //Error
				
				dev.setCommunicationState(CommunicationState.BUSY);
				dev.setAlarmState(AlarmState.MALFUNCTION);
				dev.setActivityState(ActivityStateFocuser.ERROR);
				dev.setActivityStateDesc("Error initializing the FocuserDriver, the driver is not ready.");
				
			}else { //No error.
				
				dev.setCommunicationState(CommunicationState.READY);
				dev.setAlarmState(AlarmState.NONE);
				dev.setActivityState(ActivityStateFocuser.READY);
				dev.setActivityStateDesc("");
				
			}

			dev.setBlockState(BlockState.UNBLOCK);
			
			//Other additional information
			eu.gloria.rt.entity.environment.config.device.Device devConfig = DeviceRTD.configDeviceManager.getDevice(this.getDeviceId());
			dev.setDescription(devConfig.getDescription());
			dev.setMeasureUnit(MeasureUnit.NONE);	
			dev.setShortName(devConfig.getShortName());
			dev.setType(DeviceType.FOCUS);
			dev.setVersion(devConfig.getVersion());
			
			return dev;
			
		/*}catch(RTException ex){
			LogUtil.severe(this, ex.getMessage() + ". Error code=" + ex.getErrorCode().toString());
			throw ex;*/
		}catch(Exception ex){
			LogUtil.severe(this, ex.getMessage());
			throw new RTException(ex.getMessage());
		}finally{
			rtc.stop();
			rtc.writeLog("getDevice");
		}
	}

	@Override
	public long focGetMinStep() throws RTException {
		throw new UnsupportedOpException ("Operation not supported");
	}

}
