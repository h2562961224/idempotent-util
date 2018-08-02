# 幂等小注解
一个轻量的（？）利用缓存实现的幂等注解

### 幂等
一段时间内，方法调用的幂等效果（返回上一次成功调用的返回值@Idempotent）；

方法调用中，再次调用的处理（DoingExecutor）；

目前尚不完善的从参数中取获取key（@IdempotentParam）

### 可扩展
缓存默认使用guava cache，可通过实现并注入Cacher，扩展使用其他缓存（如redis）

DoingExecutor 方法调用中时再次发起调用触发的executor，可处理重复提交，默认两个实现（DefaultValueDoingExecutor，DefaultExceptionDoingExecutor）分别对应bean默认值，直接抛错



### 代码demo
```java
//使用幂等工具
@EnableUseIdempotent
public class TestMain {
    @Data
    public static class Foo{
        private Long id;
        private String name;
        private Integer gender;
    }

    //方法开启幂等，expireMilliSecond为幂等超时时间
    @Idempotent(value = "testMain" ,expireMilliSecond = 180000)
    public Foo test(@IdempotentParam(fields = {"id","name"}) /* 用于确定用于幂等的key,fields为空则调用对象toString方法生成key */ Foo foo){
        return foo;
    }
}

```



