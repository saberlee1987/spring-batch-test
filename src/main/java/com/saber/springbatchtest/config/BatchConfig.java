package com.saber.springbatchtest.config;

import com.saber.springbatchtest.dto.Person;
import com.saber.springbatchtest.job.JobCompletionNotificationListener;
import com.saber.springbatchtest.processor.PersonItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig {
    @Value("file:xml/output.xml")
    private WritableResource outputXml;

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names("firstName", "lastName")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    /*
    writer(DataSource) creates an ItemWriter.
    This one is aimed at a JDBC destination and automatically gets a copy of the dataSource created
        by @EnableBatchProcessing.
     It includes the SQL statement needed to insert a single Person, driven by Java bean properties.
     */
    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("insert into people (first_name,last_name) values (:firstName,:lastName)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Person.class);
        return marshaller;
    }

    @Bean(name = "personItemWriterXml")
    public ItemWriter<Person> personItemWriterXml(Marshaller marshaller) {
        StaxEventItemWriter<Person> staxEventItemWriter = new StaxEventItemWriter<>();
        staxEventItemWriter.setMarshaller(marshaller);
        staxEventItemWriter.setRootTagName("Person");
        staxEventItemWriter.setResource(outputXml);
        return staxEventItemWriter;
    }


    @Bean(name = "importUserJob")
    public Job importUser(JobRepository jobRepository, JobCompletionNotificationListener listener,
                          Step step1) {
        return new JobBuilder("importUserJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean(name = "jobPersonXml")
    public Job jobXml(JobRepository jobRepository, @Qualifier(value = "stepXml") Step stepXml) {
        return new JobBuilder("jobPersonXml", jobRepository).flow(stepXml).end()
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      JdbcBatchItemWriter<Person> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Person, Person>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean(name = "stepXml")
    public Step stepXml(JobRepository jobRepository,
                        PlatformTransactionManager transactionManager
            , ItemWriter<Person> personItemWriterXml) {
        return new StepBuilder("stepXml", jobRepository)
                .<Person, Person>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(personItemWriterXml)
                .build();
    }
}
