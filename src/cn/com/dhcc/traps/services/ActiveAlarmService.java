package cn.com.dhcc.traps.services;

import java.util.Collections;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cn.com.dhcc.commons.HibernateUtil;
import cn.com.dhcc.traps.dao.ActiveAlarmDao;
import cn.com.dhcc.traps.dao.SyslogRealTimeLogDao;
import cn.com.dhcc.traps.models.ActiveAlarm;
import cn.com.dhcc.traps.models.SyslogRealTimeLog;

public class ActiveAlarmService {
	
	private ActiveAlarmDao dao;
	
	/**
	 * @param dao
	 */
	public ActiveAlarmService() {
		super();
		this.dao = new ActiveAlarmDao();
	}

	public List< ActiveAlarm> quaryAllNonSended(){
		Transaction transaction = HibernateUtil.getCurrentSession().beginTransaction();
		try{
			List<ActiveAlarm> list = dao.quaryAllNonSended();
			transaction.commit();
			return list;
		}catch(HibernateException e){
			transaction.rollback();
			e.printStackTrace();
		}
		return Collections.EMPTY_LIST;
	}
	

	public void sended(ActiveAlarm log){
		Transaction transaction = HibernateUtil.getCurrentSession().beginTransaction();
		try{
			dao.update(log);
			transaction.commit();
		}catch(HibernateException e){
			transaction.rollback();
			e.printStackTrace();
		}
	}
	public void sended(List<ActiveAlarm> logs){
		Transaction transaction = HibernateUtil.getCurrentSession().beginTransaction();
		try{
			for(ActiveAlarm log:logs){
				log.setFlag(1);
				dao.update(log);
			}
			transaction.commit();
		}catch(HibernateException e){
			transaction.rollback();
			e.printStackTrace();
		}
	}
}
