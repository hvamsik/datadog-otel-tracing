import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.exporters.datadog.DatadogSpanExporter;
import io.opentelemetry.exporters.datadog.DatadogTraceConfiguration;

public class YourJavaApp {
    public static void main(String[] args) {
        // Configure Datadog exporter
        DatadogSpanExporter exporter = DatadogSpanExporter.builder()
            .configure(DatadogTraceConfiguration.builder().build())
            .build();
        
        // Get the OpenTelemetry instance
        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
            .setTracerProvider(SdkTracerProvider.builder().build())
            .addSpanProcessor(SimpleSpanProcessor.create(exporter))
            .build();

        // Get the tracer
        Tracer tracer = openTelemetry.getTracer("your-instrumentation-name");

        // Create a root span
        SpanBuilder spanBuilder = tracer.spanBuilder("your-span-name");
        Span rootSpan = spanBuilder.startSpan();

        // Add custom span tags
        rootSpan.setAttribute("custom-tag-key", "custom-tag-value");

        try (Scope scope = rootSpan.makeCurrent()) {
            // Your application code here
            
            // Create a child span
            Span childSpan = tracer.spanBuilder("child-span-name").startSpan();
            
            // Add custom tags to the child span
            childSpan.setAttribute("child-span-tag-key", "child-span-tag-value");
            
            try (Scope childScope = childSpan.makeCurrent()) {
                // Your child span's code here
            } finally {
                childSpan.end(); // End the child span
            }
        } finally {
            rootSpan.end(); // End the root span
        }
    }
}
