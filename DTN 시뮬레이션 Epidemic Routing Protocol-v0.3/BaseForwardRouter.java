package routing;

import java.util.ArrayList;
import java.util.List;

import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;

public class BaseForwardRouter extends ActiveRouter{
	public BaseForwardRouter(Settings s) {
		super(s);
		//TODO: read&use BaseRouter specific settings (if any)
	}
	
	protected BaseForwardRouter(BaseForwardRouter r) {
		super(r);
		//TODO: copy BaseRouter settings here (if any)
	}
	
	@Override
	public void update() {
		super.update();
		if (isTransferring() || !canStartTransfer()) {
			return;
		}
		if (exchangeDeliverableMessages() != null) {
			return;
		}
		messageForwarder();
	}
	
	public void messageForwarder(){
		for (Connection con : getConnections()) {
			DTNHost other = con.getOtherNode(getHost());
			BaseForwardRouter othRouter = (BaseForwardRouter)other.getRouter();
			
			if (othRouter.isTransferring()) {
				continue;
			}
			
			List<Message> messages 
						= new ArrayList<Message>(this.getMessageCollection());
			
			if(messages.size() != 0){
				Message m = this.tryAllMessages(con, messages);
				
				if(m != null){
					System.out.println("==== 1. Messsag Forwarder ====");
					System.out.println("("+SimClock.getTime()+")");
					System.out.println("X: "+this.getHost()+", "
										+this.getMessageCollection());
					System.out.println("Y: "+other+", "
										+othRouter.getMessageCollection());
					System.out.println(this.getHost()+" forwards "
										+m.getId()+" to "+other);
					System.out.println();
				}
			}
		}
	}
	

	@Override
	protected void transferDone(Connection con) {
		String mid = con.getMessage().getId();
		Message m = getMessage(mid);
		
		if (m == null) {
			return;
		}
		
		System.out.println("==== 2.1. Transfer Done ====");
		System.out.println("("+SimClock.getTime()+")");
		System.out.println("X: "+this.getHost()+" forwarded "+m.getId());
		System.out.println(this.getMessageCollection());
		System.out.println();
	}
	
	@Override
	public Message messageTransferred(String mid, DTNHost from) {
		Message m = super.messageTransferred(mid, from);
		
		System.out.println("==== 2.2. Messsag Transferred ====");
		System.out.println("("+SimClock.getTime()+")");
		System.out.println("Y: "+this.getHost()+" received "+m.getId());
		System.out.println(this.getMessageCollection());
		System.out.println();
		return m;
	}
	
	@Override
	public BaseForwardRouter replicate(){
		return new BaseForwardRouter(this);
	}
}










