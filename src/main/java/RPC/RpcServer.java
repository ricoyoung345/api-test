package RPC;

import java.io.IOException;

/**
 * 服务端
 * <p>
 * 作者：余天然 on 2017/1/4 下午6:27
 */
public class RpcServer {

	public static void main(String[] args) {
		try {
			//暴露服务
			HelloService service = new HelloServiceImpl();
			RpcFramework.export(service, 8989);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 服务端对接口的具体实现
	 */
	private static class HelloServiceImpl implements HelloService {
		@Override
		public String sayHello(String msg) {
			String result = "hello world " + msg;
			System.out.println(result);
			return result;
		}
	}
}