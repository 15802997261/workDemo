import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;
public class App 
{
    public static void main( String[] args )
    {
        try {
            MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("192.168.92.128:11211")) ;
            builder.addAuthInfo(AddrUtil.getOneAddress("192.168.92.128:11211"), AuthInfo.plain("azul", "xxxx"));
            builder.setCommandFactory(new BinaryCommandFactory());
            builder.setFailureMode(true);
            builder.setConnectionPoolSize(10); 
            MemcachedClient client = builder.build();
            client.set("lwk", 0, "hello");
            String value = client.get("key");
            System.out.println(value);
            client.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MemcachedException e) {
            e.printStackTrace();
        }
        System.out.println( "end..." );
    }
	//Œﬁ√‹¬Î
	 public static void main( String[] args )
    {
        try {
            MemcachedClient client = new MemcachedClient(AddrUtil.getAddresses("192.168.92.128:11211"));
            client.set("lwk", 0, "hello");
            Object value = client.get("lwk");
            System.out.println(value);
            client.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println( "end..." );
    }



}