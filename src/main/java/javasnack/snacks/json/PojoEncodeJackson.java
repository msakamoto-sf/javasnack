package javasnack.snacks.json;

import java.io.OutputStreamWriter;
import java.io.Writer;

import javasnack.snacks.json.pojo.EncodePojo;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Jackson JSON Encoder Demo : POJO to JSON
 * 
 * @see https://github.com/FasterXML/jackson
 * @see http://wiki.fasterxml.com/JacksonHome
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class PojoEncodeJackson implements Runnable {
    @Override
    public void run() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonout = objectMapper.writeValueAsString(new EncodePojo());
            System.out.println("--- simple jackson encode ---");
            System.out.println(jsonout);
            String jsonout2 = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(new EncodePojo());
            System.out.println("--- default pretty-print jackson encode ---");
            System.out.println(jsonout2);
            System.out.println("--- streaming jackson encode ---");
            JsonFactory jsonFactory = objectMapper.getFactory();
            Writer out = new OutputStreamWriter(System.out);
            JsonGenerator jg = jsonFactory.createGenerator(out);
            jg.setPrettyPrinter(new DefaultPrettyPrinter());
            jg.writeStartObject();
            jg.writeStringField("message", "success");
            jg.writeNumberField("count", 10);
            jg.writeArrayFieldStart("records");
            for (int i = 0; i < 10; i++) {
                jg.writeObject(new EncodePojo());
                Thread.sleep(100);
                jg.flush();
            }
            jg.writeEndArray();
            jg.writeEndObject();
            jg.close();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
