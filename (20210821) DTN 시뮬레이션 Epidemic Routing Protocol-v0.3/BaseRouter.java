package routing;

import core.Settings;
import core.SimClock;
import core.DTNHost;

public class BaseRouter extends ActiveRouter{
	public BaseRouter(Settings s) {
		super(s);
		//TODO: read&use BaseRouter specific settings (if any)
	}
	
	protected BaseRouter(BaseRouter r) {
		super(r);
		//TODO: copy BaseRouter settings here (if any)
	}
	
	@Override
	public void update() {
		super.update();
		
		double time = SimClock.getTime();
		DTNHost host = this.getHost();
		String nodeId = host.toString();
		int nodeNumber = host.getAddress();
		
		System.out.println("["+time+"] "+nodeId+", "+nodeNumber);
	}
	
	@Override
	public BaseRouter replicate(){
		return new BaseRouter(this);
	}
}
