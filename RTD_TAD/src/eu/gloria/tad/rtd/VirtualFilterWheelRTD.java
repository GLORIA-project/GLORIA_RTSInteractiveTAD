package eu.gloria.tad.rtd;

import java.util.ArrayList;
import java.util.List;

import eu.gloria.rt.entity.device.ActivityState;
import eu.gloria.rt.entity.device.ActivityStateFilter;
import eu.gloria.rt.entity.device.AlarmState;
import eu.gloria.rt.entity.device.BlockState;
import eu.gloria.rt.entity.device.CommunicationState;
import eu.gloria.rt.entity.device.Device;
import eu.gloria.rt.entity.device.DeviceFilter;
import eu.gloria.rt.entity.device.DeviceType;
import eu.gloria.rt.entity.device.FilterType;
import eu.gloria.rt.exception.RTException;
import eu.gloria.rt.exception.UnsupportedOpException;
import eu.gloria.rtd.RTDFilterWheelInterface;

/**
 * Virtual filter WheelRTD. H_ALPHA filter fixed.
 * 
 * @author jcabello
 *
 */
public class VirtualFilterWheelRTD extends DeviceRTD implements RTDFilterWheelInterface  {

	public VirtualFilterWheelRTD(String deviceId) {
		super(deviceId);
	}

	@Override
	public String fwGetCamera() throws RTException {
		
		eu.gloria.rt.entity.environment.config.device.DeviceProperty mapping = DeviceRTD.configDeviceManager.getProperty(this.getDeviceId(), "ASSOCIATED_CAMERA");
		return mapping.getDefaultValue();
		
	}

	@Override
	public List<String> fwGetFilterList() throws RTException {
		List<String> result = new ArrayList<String>();
		result.add(FilterType.H_ALPHA.toString());
		return result;
	}

	@Override
	public int fwGetPositionNumber() throws RTException {
		return 0;
	}

	@Override
	public int fwGetSpeedSwitching() throws RTException {
		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public float fwGetFilterSize() throws RTException {
		throw new UnsupportedOpException ("Operation not supported");
	}

	@Override
	public String fwGetFilterKind() throws RTException {
		return FilterType.H_ALPHA.toString();
	}

	@Override
	public boolean fwIsAtHome() throws RTException {
		return true;
	}

	@Override
	public void fwSetOffset(List<Integer> positions) throws RTException {
		throw new UnsupportedOpException ("Operation not supported");
		
	}

	@Override
	public void fwSelectFilterKind(String kind) throws RTException {
		if (!FilterType.H_ALPHA.toString().equals(kind)){
			throw new RTException("Invalid filter");
		}
		
	}

	@Override
	public void fwSelectFilterPosition(int position) throws RTException {
		if (position != 0){
			throw new RTException("Invalid filter");
		}
		
	}

	@Override
	public void fwGoHome() throws RTException {
		//Always at home...
	}

	@Override
	public Device devGetDevice(boolean allProperties) throws RTException {
		
		DeviceFilter devFilter = new DeviceFilter();
		devFilter.setBlockState(BlockState.UNBLOCK);
		devFilter.setAlarmState(AlarmState.NONE);
		devFilter.setActivityState(ActivityStateFilter.READY);
		devFilter.setCommunicationState(CommunicationState.READY);
		devFilter.setActivityStateDesc("");
		devFilter.setType(DeviceType.FW);
		devFilter.setError(null);
		devFilter.setDescription("Virtual filter wheeel.");
		devFilter.setInfo("Virtual filter wheel.");
		devFilter.setShortName(this.getDeviceId());
		devFilter.setVersion("1.0");
		
		return devFilter;
	}

}
