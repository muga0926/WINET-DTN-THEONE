package routing;

import java.util.ArrayList;
import java.util.List;
import util.Tuple;

import core.Connection;
import core.DTNHost;
import core.Settings;
import core.Message;

public class BaseEpidemicRouter extends ActiveRouter{
	public BaseEpidemicRouter(Settings s) {
		super(s);
	}
	
	protected BaseEpidemicRouter(BaseEpidemicRouter r) {
		super(r);
	}
	
	@Override
	public void update() {
		super.update();
		if (isTransferring() || !canStartTransfer()) {
			return; //A
		}
		if (exchangeDeliverableMessages() != null) {
			return; //B
		}
		baseEpidemicMessageForwarder();
	}
	
	public List<String> checkSummaryVector(BaseEpidemicRouter othRouter){
		List<String> candidateMessages = new ArrayList<String>();
		for(Message m : getMessageCollection()){
			if(!othRouter.hasMessage(m.getId())){
				candidateMessages.add(m.getId());
			}
		}
		return candidateMessages;
	}
	
	public void baseEpidemicMessageForwarder(){
		List<Tuple<Message, Connection>> messages = new ArrayList<Tuple<Message, Connection>>();
		for (Connection con : getConnections()) {
			DTNHost other = con.getOtherNode(getHost());
			BaseEpidemicRouter othRouter = (BaseEpidemicRouter)other.getRouter();
			if (othRouter.isTransferring()) {
				continue;
			}
			List<String> candidateMessages = new ArrayList<String>(checkSummaryVector(othRouter));
			for (String mid : candidateMessages) {
				if(this.hasMessage(mid)){
					messages.add(new Tuple<Message, Connection>(this.getMessage(mid),con));
				}
			}
		}
		if (messages.size() != 0) {
			this.sortByQueueMode(messages);
			tryMessagesForConnected(messages);
		}
	}
	
	@Override
	public BaseEpidemicRouter replicate(){
		return new BaseEpidemicRouter(this);
	}
}















