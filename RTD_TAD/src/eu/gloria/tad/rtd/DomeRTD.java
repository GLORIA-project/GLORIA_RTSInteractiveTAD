package eu.gloria.tad.rtd;


import org.ciclope.server.dome.controllers.CommunicationException;
import org.ciclope.server.dome.controllers.ControllerException;
import org.ciclope.server.dome.controllers.TadDomeSocketImpl;

import eu.gloria.rt.entity.device.ActivityStateDome;
import eu.gloria.rt.entity.device.ActivityStateDomeOpening;
import eu.gloria.rt.entity.device.ActivityStateMount;
import eu.gloria.rt.entity.device.AlarmState;
import eu.gloria.rt.entity.device.BlockState;
import eu.gloria.rt.entity.device.CommunicationState;
import eu.gloria.rt.entity.device.Device;
import eu.gloria.rt.entity.device.DeviceDome;
import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.entity.device.MeasureUnit;
import eu.gloria.rt.entity.environment.config.device.DeviceProperty;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rtc.DeviceDiscoverer;
import eu.gloria.rtd.RTDDomeInterface;
import eu.gloria.tools.log.LogUtil;
import eu.gloria.tools.time.RunTimeCounter;

/**
 * TAD - DOME - RTD
 * 
 * @author mclopez
 * 
 * Pending:
 *  
 *   
 * Error:
 *   
 *
 */
public class DomeRTD extends DeviceRTD implements RTDDomeInterface {
	

	/**
	 * Internal Service.
	 */
	private TadDomeSocketImpl domeImpl = null;
	
	/**
	 * Part of the dome by configuration.
	 */
	private int configIndex = 0;

	
	public static void main(String[] args) {
		
		try {
			
			/*Socket s = new Socket("161.72.128.11", 2000);
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			out.writeBytes("hola caracola");
			//out.write(command.getBytes());
			out.flush();*/
			
			
			
			DomeRTD rtd = new DomeRTD("dome");
			
			DeviceDome dev = (DeviceDome) rtd.devGetDevice(true);
			
			System.out.println(dev.getActivityState()+"\n");
			System.out.println(dev.getActivityStateOpening()+"\n");
			
//			rtd.domClose(1);
//			
//			Thread.sleep(500);
//			
//			dev = rtd.getDevice();
//			
//			rtd.domOpen(1);
//			
			
			//rtd.domOpen(1);
//			dev = rtd.devGetDevice(true);
			
			
			System.out.println("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
		
	public DomeRTD (String deviceId) throws RTException {
		
		super(deviceId);
		
		RunTimeCounter rtc = new RunTimeCounter("DomeRTD", this.getDeviceId());
		rtc.start();
		
		readConfig();
		
		rtc.stop();
		rtc.writeLog("DomeRTD");
		
	}
	
	private void readConfig() throws RTException {
		
		try{
			
			LogUtil.info(this, "Dome->Reading configuration.");
			
			DeviceProperty configIndexProp = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "CONFIG_INDEX");
			DeviceProperty ipProp = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "IP");
			DeviceProperty portProp = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "PORT");
			
			String[] names ={"CONFIG_INDEX", "IP", "PORT"};
			String[] values = {configIndexProp.getDefaultValue(), ipProp.getDefaultValue(), portProp.getDefaultValue()};
			LogUtil.info(this, "Dome Config =" + LogUtil.getLog(names, values));
			
			
			configIndex = Integer.parseInt(configIndexProp.getDefaultValue());
			
			domeImpl = new TadDomeSocketImpl(ipProp.getDefaultValue(), Integer.parseInt(portProp.getDefaultValue()));
			
		}catch(Exception ex){
			LogUtil.severe(this, "Dome->Constructor. Error:" + ex.getMessage());
			//Exception not thrown to get the device in error state
//			if (ex.getMessage() != null && ex.getMessage().indexOf("Not driver found") ==  -1){
//				throw new RTException(ex.getMessage());
//			}
		}
		
		LogUtil.info(this, "Dome->Read configuration.");
		
		
	}
	
	
	@Override
	public int domGetNumberElement() throws RTException {
		
		return 1;
	}

	@Override
	public boolean domCanSetAltitude() throws RTException {
		
		return false;
	}

	@Override
	public boolean domCanSetAzimuth() throws RTException {
		
		return false;
	}

	@Override
	public boolean domCanSetPark() throws RTException {
		
		return false;
	}

	@Override
	public boolean domIsAtHome() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public boolean domIsAtPark() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public double domGetAltitude() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public double domGetAzimuth() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	@Override
	public void domOpen(int element) throws RTException {

		RunTimeCounter rtc = new RunTimeCounter("DomeRTD", this.getDeviceId());
		rtc.start();
		
		LogUtil.info(this, "Dome->domOpen( " + element + "). BEGIN");
		
		ActivityStateDomeOpening state = ((DeviceDome) DeviceDiscoverer.devGetDevice(this.getDeviceId(), false)).getActivityStateOpening();
		
		LogUtil.info(this, "Dome->domOpen( " + element + "). C.P.01");
		
		if ( state == ActivityStateDomeOpening.CLOSE || state == ActivityStateDomeOpening.NOT_DEFINED){
			try {
				LogUtil.info(this, "Dome->domOpen( " + element + "). C.P.02");
				domeImpl.open(TadDomeSocketImpl.RIGHT_BOX);	//RIGHT_BOX
			/*} catch (ControllerException e) {
				throw new RTException (e.getMessage());*/
			} catch (CommunicationException e) {
				LogUtil.severe(this, "Dome->domOpen( " + element + "). Communication ERROR:" + e.getMessage());
				throw new RTException (e.getMessage());
			}	
		}else{
			LogUtil.severe(this, "Dome->domOpen( " + element + "). The dome is not closed");
			throw new RTException ("Dome is not closed");
		}
		
		LogUtil.info(this, "Dome->domOpen( " + element + "). END");
		
		rtc.stop();
		rtc.writeLog("domOpen");

	}

	@Override
	public void domClose(int element) throws RTException {

		RunTimeCounter rtc = new RunTimeCounter("DomeRTD", this.getDeviceId());
		rtc.start();
		
		LogUtil.info(this, "Dome->domClose( " + element + "). BEGIN");
		
		ActivityStateDomeOpening state = ((DeviceDome) DeviceDiscoverer.devGetDevice(this.getDeviceId(), false)).getActivityStateOpening();
		
		LogUtil.info(this, "Dome->domClose( " + element + "). C.P.01");
		
		if (state == ActivityStateDomeOpening.OPEN || state == ActivityStateDomeOpening.NOT_DEFINED){
			try {
				LogUtil.info(this, "Dome->domClose( " + element + "). C.P.02");
				domeImpl.close(TadDomeSocketImpl.RIGHT_BOX);	//RIGHT_BOX
			/*} catch (ControllerException e) {
				throw new RTException (e.getMessage());
			}*/
			} catch (CommunicationException e) {
				LogUtil.severe(this, "Dome->domClose( " + element + "). Communication Error:" + e.getMessage());
				throw new RTException (e.getMessage());
			}
		}else{
			LogUtil.severe(this, "Dome->domClose( " + element + "). The dome is not open");
			throw new RTException ("Dome is not open");
		}
		
		LogUtil.info(this, "Dome->domClose( " + element + "). END");
		
		
		rtc.stop();
		rtc.writeLog("domClose");


	}

	@Override
	public void domGoHome() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");

	}

	@Override
	public void domSetPark(double altitude, double azimuth) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");

	}

	@Override
	public void domPark() throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	@Override
	public void domMoveAzimuth(double azimuth) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");

	}

	@Override
	public void domMoveAltitude(double altitude) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");

	}

	@Override
	public void domSetTracking(boolean value) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}

	@Override
	public boolean domGetTracking() throws RTException {
		
		throw new UnsupportedOpException ("Operation not supported");
		
	}

	@Override
	public void domSlewObject(String object) throws RTException {

		throw new UnsupportedOpException ("Operation not supported");
		
	}
	
	@Override
	public Device devGetDevice(boolean allProperties) throws RTException {
		
		RunTimeCounter rtc = new RunTimeCounter("DomeRTD", this.getDeviceId());
		rtc.start();
		
		try{
			
			//Based on the xml state file.
			boolean error = false;
			String xml = null;
			DomeStateXmlParser state = null;
			
			//for (int x = 0; x < 10; x++){
				
			//	error = false;
			//	xml = null;
			//	state = null;
			
				try{	
					LogUtil.info(this, "Dome->Requesting the state........");
					xml = domeImpl.getStatusXML();
					LogUtil.info(this, "Dome->xml::" + xml);
					state = new DomeStateXmlParser(xml);				
				}catch(Exception ex){
					LogUtil.severe(this, "Dome->Error recovering the Dome state. " + ex.getMessage());
					error = true;
				}
				
			//	if (state.getState() != DomeState.NOT_DEFINED){ //Because the real driver lost (sometimes) the state value...
			//		break;
			//	}
				
			//	Thread.sleep(500);
				
			//}
			
			
			DeviceDome dev = new DeviceDome();
				
			//	Resolve the activity state
			if (error){ //Error
				
				LogUtil.info(this, "Dome->in ERROR");
				
				dev.setCommunicationState(CommunicationState.BUSY);
				dev.setAlarmState(AlarmState.MALFUNCTION);
				dev.setActivityState(ActivityStateDome.STOP); //FIXED
				dev.setActivityStateOpening(ActivityStateDomeOpening.ERROR);
				
			}else { //No error.
				
				LogUtil.info(this, "Dome->Working");
				
				dev.setCommunicationState(CommunicationState.READY);
				dev.setAlarmState(AlarmState.NONE);
				dev.setActivityState(ActivityStateDome.STOP); //FIXED
				
				if (state.getState() == DomeState.ERROR){
					dev.setActivityStateOpening(ActivityStateDomeOpening.ERROR);
				} else if (state.getState() == DomeState.CLOSED_BOXES){
					dev.setActivityStateOpening(ActivityStateDomeOpening.CLOSE);
				} else if (state.getState() == DomeState.OPENED_BOXES){
					dev.setActivityStateOpening(ActivityStateDomeOpening.OPEN);
				} else if (state.getState() == DomeState.RIGHT_BOX_OPENED){
					dev.setActivityStateOpening(ActivityStateDomeOpening.OPEN);
				} else if (state.getState() == DomeState.RIGHT_BOX_CLOSED){
					dev.setActivityStateOpening(ActivityStateDomeOpening.CLOSE);				
				} else if (state.getState() == DomeState.NOT_DEFINED){
					dev.setActivityStateOpening(ActivityStateDomeOpening.NOT_DEFINED);
				}	
				
				LogUtil.info(this, "Dome->C.P.1- Resolved state");
			}

			dev.setBlockState(BlockState.UNBLOCK);
			dev.setActivityStateDesc("");
			
			//Other additional information
			eu.gloria.rt.entity.environment.config.device.Device devConfig = DeviceRTD.configDeviceManager.getDevice(this.getDeviceId());
			dev.setDescription(devConfig.getDescription());
			dev.setMeasureUnit(MeasureUnit.NONE);	
			dev.setShortName(devConfig.getShortName());
			dev.setType(DeviceType.DOME);
			dev.setVersion(devConfig.getVersion());
			
			LogUtil.info(this, "Dome->C.P.2- Returning device.");
			
			return dev;
			
		/*}catch(RTException ex){
			LogUtil.severe(this, ex.getMessage() + ". Error code=" + ex.getErrorCode().toString());
			throw ex;*/
		}catch(Exception ex){
			LogUtil.severe(this,  "Dome->ERROR!!!: " + ex.getMessage());
			throw new RTException(ex.getMessage());
		}finally{
			rtc.stop();
			rtc.writeLog("getDevice");
		}
	}
	

}
