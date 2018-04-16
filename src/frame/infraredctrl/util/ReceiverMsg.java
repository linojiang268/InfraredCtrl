package frame.infraredctrl.util;

public class ReceiverMsg {
	public byte cmd;
	public String mac;
	public String mark;
	public String content;

	public ReceiverMsg(byte cmd, String mac, String mark, String content) {
		super();
		this.cmd = cmd;
		this.mac = mac;
		this.mark = mark;
		this.content = content;
	}

}
