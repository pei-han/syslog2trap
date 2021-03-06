package cn.com.dhcc.traps;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/** 
 * 本类用于向管理进程发送Trap信息 
 *  
 * @author HP 
 * 
 */  
public class TrapSender {  
  
	private  static String IP ;    //对端IP
	public  static long SYS_UP_TIME ;
	private  static String PORT ;
    private   Snmp snmp = null;  
    
    static{
			preInit();
    }
    
    /**
	 * @return the iP
	 */
	public static String getIP() {
		return IP;
	}
	/**
	 * @return the pORT
	 */
	public static String getPORT() {
		return PORT;
	}
	private static void preInit() {
    	try {
			Properties properties = new Properties();
			properties.load(new InputStreamReader(TrapSender.class.getResourceAsStream("/trap.properties")));
			TrapSender.IP = properties.getProperty("ip");
			TrapSender.PORT = properties.getProperty("port");
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    /**
	 * 
	 */
	public TrapSender() {
		super();
		// TODO Auto-generated constructor stub
		this.version = SnmpConstants.version1;
		init();
	}

	private int version;//默认v1版本
    /**
	 * @param version
	 */
	public TrapSender(int version) {
		super();
		this.version = version;
		init();
	}
	private void init(){

        // 设置管理进程的IP和端口  
	   Address targetAddress   = GenericAddress.parse("udp:"+IP+"/"+PORT); 
        try {
            TransportMapping transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			
			 // 设置 target  
	        target = new CommunityTarget();  
	        target.setAddress(targetAddress);  
	        // 通信不成功时的重试次数  
	        target.setRetries(2);  
	        // 超时时间  
	        target.setTimeout(1500);  
	        // snmp版本  
	        target.setVersion(this.version);  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	private static CommunityTarget target = null;
  
	private static final Log LOGGER = LogFactory.getLog(TrapSender.class);
    /** 
     * 向管理进程发送Trap报文 
     *  
     * @throws IOException 
     */  
    public  void sendPDU(PDUv1 pdu) throws IOException {  

      
    	LOGGER.info(pdu);
        // 向Agent发送PDU，并接收Response  
        ResponseEvent respEvnt = snmp.send(pdu, target);  
  
        // 解析Response  
       if (respEvnt != null && respEvnt.getResponse() != null) {  
            Vector<VariableBinding> recVBs = (Vector<VariableBinding>) respEvnt.getResponse()  
            .getVariableBindings();  
            for (int i = 0; i < recVBs.size(); i++) {  
                VariableBinding recVB = recVBs.elementAt(i);  
                System.out.println(recVB.getOid() + " : " + recVB.getVariable());  
            }  
        } 
    }  
  
    public static void sendPDU(List<PDUv1> pdus){  
    	for(PDUv1 pdu : pdus){
    		try {
    			TrapSender sender = new TrapSender();
    			sender.sendPDU(pdu);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
}  