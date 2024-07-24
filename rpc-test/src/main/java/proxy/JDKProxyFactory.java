package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-22
 * @Description:
 */
public class JDKProxyFactory {
    public static Object getProxy(Object target){
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("method = " + method.getName());
                        Object invoke = method.invoke(target, args);
                        return invoke;
                    }
                }
        );
    }
}
