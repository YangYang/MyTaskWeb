package com.imudges.web.mytask;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

@SetupBy(value=MainSetup.class)
@IocBy(type=ComboIocProvider.class, args={"*js", "ioc/",
        // 这个package下所有带@IocBean注解的类,都会登记上
        "*anno", "com.imudges.web.mytask",
        "*tx", // 事务拦截 aop
        "*async"}) // 异步执行aop
@IocBean
@Modules(scanPackage=true)
public class MainModule {
}
