package cn.com.dhcc;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.LogFactory;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;

import cn.com.dhcc.traps.TrapSender;
import cn.com.dhcc.traps.models.SyslogRealTimeLog;
import cn.com.dhcc.traps.services.SyslogRealTimeLogService;
import cn.com.dhcc.traps.util.Syslog2TrapPdu;

public class Main {

	public static void main(String[] args){
		Timer timer = new Timer();
		TrapSender.SYS_UP_TIME = new Date().getTime();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
		SyslogRealTimeLogService service = new SyslogRealTimeLogService();

				List< SyslogRealTimeLog> logs = service.quaryAllNonSended();
				List<PDUv1> pdus = Syslog2TrapPdu.toPdu(logs);
				TrapSender.sendPDU(pdus);
			    service.sended(logs);
			}
			
		}, 0, 20*1000);
	}
}