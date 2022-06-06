package com.dingdongdeng.coinautotrading.trading.domain;

import java.sql.Connection;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@Slf4j
@DataJpaTest
public class DataSourceTest {

  @Autowired private DataSource dataSource;

  @Test
  public void DB커넥션_테스트() throws Exception {
    Connection con = dataSource.getConnection();
    log.info("driver name : {}", con.getMetaData().getDriverName());
  }
}
