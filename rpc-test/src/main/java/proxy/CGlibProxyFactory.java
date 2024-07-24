package proxy;

import org.springframework.cglib.proxy.Enhancer;

/**
 * Created with Intellij IDEA.
 *
 * @Author: zws
 * @Date: 2024-07-22
 * @Description:
 */
public class CGlibProxyFactory {
    public static Object getProxy(Class<?> cs){
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(cs.getClassLoader());
        enhancer.setSuperclass(cs);
        enhancer.setCallback(new MyMethodInterceptor());
        return enhancer.create();
    }
}
