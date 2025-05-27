-- BATCH_JOB_INSTANCE
CREATE TABLE BATCH_JOB_INSTANCE  (
                                     JOB_INSTANCE_ID BIGINT NOT NULL PRIMARY KEY,
                                     VERSION BIGINT,
                                     JOB_NAME VARCHAR(100) NOT NULL,
                                     JOB_KEY VARCHAR(32) NOT NULL,
                                     CONSTRAINT JOB_INST_UN UNIQUE (JOB_NAME, JOB_KEY)
);

-- BATCH_JOB_EXECUTION
CREATE TABLE BATCH_JOB_EXECUTION  (
                                      JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                      VERSION BIGINT,
                                      JOB_INSTANCE_ID BIGINT NOT NULL,
                                      CREATE_TIME TIMESTAMP NOT NULL,
                                      START_TIME TIMESTAMP DEFAULT NULL,
                                      END_TIME TIMESTAMP DEFAULT NULL,
                                      STATUS VARCHAR(250),
                                      EXIT_CODE VARCHAR(2500),
                                      EXIT_MESSAGE VARCHAR(2500),
                                      LAST_UPDATED TIMESTAMP,
                                      JOB_CONFIGURATION_LOCATION VARCHAR(2500) NULL,
                                      CONSTRAINT JOB_INST_EXEC_FK FOREIGN KEY (JOB_INSTANCE_ID)
                                          REFERENCES BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
);

-- BATCH_JOB_EXECUTION_PARAMS
CREATE TABLE BATCH_JOB_EXECUTION_PARAMS  (
                                             JOB_EXECUTION_ID BIGINT NOT NULL,
                                             PARAMETER_NAME VARCHAR(100) NOT NULL,
                                             PARAMETER_TYPE VARCHAR(250) NOT NULL,
                                             PARAMETER_VALUE VARCHAR(250) DEFAULT NULL,
                                             IDENTIFYING CHAR(1) NOT NULL,
                                             CONSTRAINT JOB_EXEC_PARAMS_FK FOREIGN KEY (JOB_EXECUTION_ID)
                                                 REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

-- BATCH_STEP_EXECUTION
CREATE TABLE BATCH_STEP_EXECUTION  (
                                       STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                       VERSION BIGINT NOT NULL,
                                       STEP_NAME VARCHAR(100) NOT NULL,
                                       JOB_EXECUTION_ID BIGINT NOT NULL,
                                       CREATE_TIME TIMESTAMP NOT NULL,
                                       START_TIME TIMESTAMP NOT NULL,
                                       END_TIME TIMESTAMP DEFAULT NULL,
                                       STATUS VARCHAR(10),
                                       COMMIT_COUNT BIGINT DEFAULT NULL,
                                       READ_COUNT BIGINT DEFAULT NULL,
                                       FILTER_COUNT BIGINT DEFAULT NULL,
                                       WRITE_COUNT BIGINT DEFAULT NULL,
                                       READ_SKIP_COUNT BIGINT DEFAULT NULL,
                                       WRITE_SKIP_COUNT BIGINT DEFAULT NULL,
                                       PROCESS_SKIP_COUNT BIGINT DEFAULT NULL,
                                       ROLLBACK_COUNT BIGINT DEFAULT NULL,
                                       EXIT_CODE VARCHAR(2500),
                                       EXIT_MESSAGE VARCHAR(2500),
                                       LAST_UPDATED TIMESTAMP,
                                       CONSTRAINT JOB_EXEC_STEP_FK FOREIGN KEY (JOB_EXECUTION_ID)
                                           REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);
-- BATCH_STEP_EXECUTION_CONTEXT
CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT  (
                                               STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                               SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                               SERIALIZED_CONTEXT CLOB DEFAULT NULL,
                                               CONSTRAINT STEP_EXEC_CTX_FK FOREIGN KEY (STEP_EXECUTION_ID)
                                                   REFERENCES BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
);

-- BATCH_JOB_EXECUTION_CONTEXT
CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT  (
                                              JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                              SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                              SERIALIZED_CONTEXT CLOB DEFAULT NULL,
                                              CONSTRAINT JOB_EXEC_CTX_FK FOREIGN KEY (JOB_EXECUTION_ID)
                                                  REFERENCES BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

-- BATCH_STEP_EXECUTION_SEQ
CREATE SEQUENCE BATCH_STEP_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;

-- BATCH_JOB_EXECUTION_SEQ
CREATE SEQUENCE BATCH_JOB_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;

-- BATCH_JOB_SEQ
CREATE SEQUENCE BATCH_JOB_SEQ MAXVALUE 9223372036854775807 NO CYCLE;