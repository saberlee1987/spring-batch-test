package com.saber.springbatchtest.job;

import com.saber.springbatchtest.dto.PersonRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobCompletionNotificationListener implements JobExecutionListener {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED){
            log.info("!!! job finished!! time to verify the results");
            jdbcTemplate.query("select * from people",new PersonRowMapper())
                    .forEach(person -> log.info("found <{{}}> in database .",person));
        }
    }
}
