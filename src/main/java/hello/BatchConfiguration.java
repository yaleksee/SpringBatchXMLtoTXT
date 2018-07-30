package hello;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    StaxEventItemReader<Bic> itemReader() {
        StaxEventItemReader<Bic> reader = new StaxEventItemReader<Bic>();
        //reader.setResource(new UrlResource("http://www.cbr.ru/psystem/persp_ps/"));
        reader.setResource(new FileSystemResource("1.xml"));

        reader.setFragmentRootElementNames(new String[]{"Bic", "rstrList", "accRstrList"});

        Map<String, String> aliaseMap = new HashMap<>();
        aliaseMap.put("bic", "java.hello.Bic");

        XStreamMarshaller marshaller = new XStreamMarshaller();
        marshaller.setAliases(aliaseMap);

        reader.setUnmarshaller(marshaller);

        return reader;
    }

    @Bean
    public ItemProcessor<Bic, Bic> itemProcessor() {
        return new BicItemProcessor();
    }

    @Bean
    public FlatFileItemWriter itemWriter() {
        return new FlatFileItemWriterBuilder<Bic>()
                .name("itemWriter")
                .resource(new FileSystemResource("target/output.txt"))
                .lineAggregator(new PassThroughLineAggregator<>())
                .build();
    }


    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Bic, Bic>chunk(10)
                .faultTolerant()
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job importBicJob(Step s1) {
        return jobBuilderFactory.get("importBic")
                .incrementer(new RunIdIncrementer())
                .flow(s1)
                .end()
                .build();
    }
}
