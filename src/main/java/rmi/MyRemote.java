package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 远程接口
 */
public interface MyRemote extends Remote {
	public String sayHello() throws RemoteException;
}
