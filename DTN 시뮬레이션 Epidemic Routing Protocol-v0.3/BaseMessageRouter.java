package routing;

import java.util.Collection;

import core.Settings;
import core.SimClock;
import core.Message;

public class BaseMessageRouter extends ActiveRouter{
	public BaseMessageRouter(Settings s) {
		super(s);
		//TODO: read&use BaseRouter specific settings (if any)
	}
	
	protected BaseMessageRouter(BaseMessageRouter r) {
		super(r);
		//TODO: copy BaseRouter settings here (if any)
	}
	
	@Override
	public void update() {
		super.update();
		
		if(SimClock.getIntTime() % 1000 == 0){
			if(this.getHost().getAddress() == 10){
				printMessagesInMyBuffer();
			}
		}
	}
	
	public void printMessagesInMyBuffer(){
		Collection<Message> msgCollection = this.getMessageCollection();
		
		System.out.println("("+SimClock.getTime()+") "+this.getHost().toString());
		System.out.println("Collection<Message>: "+msgCollection);
		
		for(Message m : msgCollection){
			String id = m.getId();
			String src = m.getFrom().toString();
			String dst = m.getTo().toString();
			int hop = m.getHopCount();
			int ttl = m.getTtl();
			int size = m.getSize();
			
			System.out.println("["+id+"] "
					+ "src: "+src
					+", dst: "+dst
					+", hop: "+hop
					+", TTL: "+ttl
					+", size: "+size);
		}
		System.out.println();
	}
	
	@Override
	public BaseMessageRouter replicate(){
		return new BaseMessageRouter(this);
	}
}
