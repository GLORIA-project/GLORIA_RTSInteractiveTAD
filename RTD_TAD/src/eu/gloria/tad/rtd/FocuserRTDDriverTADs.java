package eu.gloria.tad.rtd;

/**
 * This driver contain code developed by UPM for TADs.
 *  
 * @author jcabello
 *
 */
public class FocuserRTDDriverTADs {
	
	/**
	 * Current absolute position.
	 */
	private long currentPos;
	
	/**
	 * The possible greatest current position.
	 */
	private long maxStep;
	
	/**
	 * The possible greatest steps increment.
	 */
	private long maxIncrement;
	
	/**
	 * Flag. true -> the focuser has been reseted.
	 */
	private boolean initilized;
	
	/**
	 * Constructor
	 */
	public FocuserRTDDriverTADs(){
		maxStep = 70000; //for instance. It's a focuser property (constant).
		maxIncrement = -1;
		initilized = false;
	}
	
	/**
	 * Move to an absolute position.
	 * @param position Absolute position.
	 * @throws Exception In error case.
	 */
	public void move(long position) throws Exception {
		
		try{
			
			if (!initilized){
				reset();
			}
			
			if (position == 0){ 
				
				//TODO
				//Look for the initial position...we have to discuss it
				
			}else{
				
				long stepInc = position - currentPos;
				
				if (maxIncrement > -1 && maxIncrement < stepInc){
					throw new Exception("MAX_INCREMENT exceeded");
				}
				
				if (position < 0 || position > maxStep){
					throw new Exception("Invalid position");
				}
				
				//All right...moving....
				
				//TODO: movement(stepInc)
			}
			
			currentPos = position;
			
		}catch(Exception ex){
			
			//The driver is not synchronized.
			initilized = false;
			throw ex;
		}
		
	}
	
	/**
	 * Reset the focuser. Move it to 0 position.
	 * @throws Exception  In error case.
	 */
	public void reset() throws Exception {
		
		//Reset the position
		move(0);
		
		initilized = true;
	}

	/**
	 * Returns the current absolute position.
	 * @return Absolute position.
	 * @throws Exception In error case.
	 */
	public long getCurrentPos() throws Exception  {
		
		if (!initilized){
			reset();
		}
		
		return currentPos;
	}
	
	
	/**
	 * Stop the movement. In fact, do nothing.
	 * @throws Exception In error case.
	 */
	public void stop() throws Exception {
		
		if (!initilized){
			reset();
		}
		
		//DO NOTHING
	}

	/**
	 * Return the possible greatest current position.
	 * @return value.
	 */
	public long getMaxStep() {
		return maxStep;
	}

	/**
	 * Return the possible greatest steps increment.
	 * @return value.
	 */
	public long getMaxIncrement() {
		return maxIncrement;
	}

}
