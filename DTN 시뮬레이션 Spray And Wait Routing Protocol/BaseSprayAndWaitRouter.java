package routing;

import java.util.ArrayList;
import java.util.List;

import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;

public class BaseSprayAndWaitRouter extends ActiveRouter{
	public static final String BASESPRAYANDWAIT_NS = "BaseSprayAndWaitRouter";
	public static final String NROF_COPIES = "nrofCopies";
	public static final String BINARY_MODE = "binaryMode";
	
	protected int initCopies;
	protected boolean isBinary;	
	
	public BaseSprayAndWaitRouter(Settings s) {
		super(s);
		Settings baseSnwSettings = new Settings(BASESPRAYANDWAIT_NS);
		initCopies = baseSnwSettings.getInt(NROF_COPIES);
		isBinary = baseSnwSettings.getBoolean(BINARY_MODE);
	}
	
	protected BaseSprayAndWaitRouter(BaseSprayAndWaitRouter r) {
		super(r);
		this.initCopies = r.initCopies;
		this.isBinary = r.isBinary;
	}
	
	@Override 
	public boolean createNewMessage(Message m) {
		makeRoomForNewMessage(m.getSize());
		m.setTtl(this.msgTtl);
		m.setCopies(initCopies);
		addToMessages(m, true);
		return true;
	}
	
	@Override
	protected void transferDone(Connection con) {
		String mid = con.getMessage().getId();
		Message m = getMessage(mid);
		
		if (m == null) {
			return;
		}
		
		int copies = m.getCopies();
		int beforeCopies = copies;
		if (isBinary) { 
			copies /= 2;
		}
		else {
			copies--;
		}
		m.setCopies(copies);
		
		if(this.getHost().getAddress() == 0){
			System.out.println("==== 2.1. Transfer Done ====");
			System.out.println("("+SimClock.getTime()+")");
			System.out.println("X(before): "+this.getHost()
					+" forwarded "+m.getId()+"("+beforeCopies+")");	
			System.out.println("X(after): "+this.getHost()
					+" forwarded "+m.getId()+"("+m.getCopies()+")");
			System.out.println();
		}
		
	}
	
	@Override
	public Message messageTransferred(String mid, DTNHost from) {
		Message m = super.messageTransferred(mid, from);
		
		int copies = m.getCopies();
		int beforeCopies = copies;
		if (isBinary) {
			copies = (int)Math.ceil(copies/2.0);
		}
		else {
			copies = 1;
		}		
		m.setCopies(copies);
		if(from.getAddress() == 0){
			System.out.println("==== 2.2. Message Transferred ====");
			System.out.println("("+SimClock.getTime()+")");
			System.out.println("Y(before): "+this.getHost()
					+" received "+m.getId()+"("+beforeCopies+")");
			System.out.println("Y(after): "+this.getHost()
					+" received "+m.getId()+"("+m.getCopies()+")");
			System.out.println();
		}
		return m;
	}
	
	public List<String> checkCopies(BaseSprayAndWaitRouter othRouter){
		List<String> candidateMessages = new ArrayList<String>();
		for(Message m : getMessageCollection()){
			if(othRouter.hasMessage(m.getId())){
				continue;
			}
			if(m.getCopies() > 1){
				candidateMessages.add(m.getId());
			}
		}
		return candidateMessages;
	}
	
	public void baseSprayAndWaitmessageForwarder(){
		for (Connection con : getConnections()) {
			DTNHost other = con.getOtherNode(getHost());
			BaseSprayAndWaitRouter othRouter = (BaseSprayAndWaitRouter)other.getRouter();
			if (othRouter.isTransferring()) {
				continue;
			}
			List<String> candidateMessages = new ArrayList<String>(checkCopies(othRouter));			
			List<Message> messages = new ArrayList<Message>();
			
			for (String mid : candidateMessages) {
				if(this.hasMessage(mid)){
					messages.add(this.getMessage(mid));
				}
			}
			if (messages.size() != 0) {
				this.sortByQueueMode(messages);
				Message ok = this.tryAllMessages(con, messages);
				if(ok != null){
					if(this.getHost().getAddress() == 0){
						System.out.println("==== 1. Messsage Forwarder ====");
						System.out.println("("+SimClock.getTime()+")");	
						System.out.println("X("+this.getHost()+") --- "
								+ok.getId()+"("+ok.getCopies()+")"+" ---> "+"Y("+other+") ");
						System.out.println();
					}
				}
			}
		}
	}
	
	@Override
	public void update() {
		super.update();
		if (!canStartTransfer() || isTransferring()) {
			return; // nothing to transfer or is currently transferring 
		}

		/* try messages that could be delivered to final recipient */
		if (exchangeDeliverableMessages() != null) {
			return;
		}
		
		baseSprayAndWaitmessageForwarder();
	}
		
	@Override
	public BaseSprayAndWaitRouter replicate(){
		return new BaseSprayAndWaitRouter(this);
	}
}























