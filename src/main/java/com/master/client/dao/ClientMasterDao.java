package com.master.client.dao;

import com.master.client.bean.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by Athul on 10/22/16.
 */
@Slf4j
@Repository
public class ClientMasterDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Client addClient(Client client)
    {
        return client;
    }
}
