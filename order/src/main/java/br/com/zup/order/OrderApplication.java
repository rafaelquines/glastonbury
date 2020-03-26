package br.com.zup.order;
//
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.samplers.ProbabilisticSampler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OrderApplication {

    //    @Bean
//    public SamplerConfiguration tracing() {
//        SamplerConfiguration samplerConfig = SamplerConfiguration.fromEnv()
//                .withType(ConstSampler.TYPE)
//                .withParam(1);
//
//        ReporterConfiguration reporterConfig = ReporterConfiguration.fromEnv()
//                .withLogSpans(true);
//
//        io.jaegertracing.Configuration config = new io.jaegertracing.Configuration("helloWorld")
//                .withSampler(samplerConfig)
//                .withReporter(reporterConfig);
//
//        GlobalTracer.register(config.getTracer());
//
//        Span parent = GlobalTracer.get().buildSpan("hello").start();
//
//        try (Scope scope = GlobalTracer.get().scopeManager()
//                .activate(parent, true)) {
//            Span child = GlobalTracer.get().buildSpan("world")
//                    .asChildOf(parent).start();
//            try (Scope scope2 = GlobalTracer.get().scopeManager()
//                    .activate(child, true)) {
//            }
//        }
//        return samplerConfig;
//    }

//    @Bean
//    public io.opentracing.Tracer jaegerTracing() {
//        Configuration.SamplerConfiguration samplerConfig = Configuration.SamplerConfiguration.fromEnv()
//                .withType(ProbabilisticSampler.TYPE)
//                .withParam(1);
//        Configuration.ReporterConfiguration reporterConfig = Configuration.ReporterConfiguration.fromEnv()
//                .withLogSpans(true);
//        return new Configuration("order-microservice")
//                .withSampler(samplerConfig)
//                .withReporter(reporterConfig)
//                .getTracer();
//    }

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
