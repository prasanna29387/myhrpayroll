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
        if(addStatement(client)==-1)
        {
            client.getErrors().add("Error while creating a new client");
        }
        return client;
    }


    public Client updateClient(Client client)
    {
        if(updateStatement(client)==-1)
        {
            client.getErrors().add("Error while updating client");
        }

        return client;
    }

    public Client deleteClient(Client client)
    {
        if(deleteStatement(client)==-1)
        {
            client.getErrors().add("Error while Deleting client");
        }

        return client;
    }


    protected int addStatement(Client client)
    {
        String sql = "insert into t_payrl_client_mstr values(?,?,?,?,?,?)";
        return jdbcTemplate.update(sql,new Object[]{client.getClientId(),client.getClientName(),client.getClientContactEmail(),
                client.getClientContactName(),client.getClientContactPhone(),client.getPanNumber()});
    }

    protected int updateStatement(Client client)
    {
        String sql = "UPDATE t_payrl_client_mstr SET CLIENT_NAME = ? ,CLIENT_CONTACT_NAME = ? ,CLIENT_EMAIL = ? " +
                ",CLIENT_PHONE=? ,CLIENT_PAN= ? WHERE CLIENT_ID=?";
        return jdbcTemplate.update(sql,new Object[]{client.getClientName(),client.getClientContactName(),
                client.getClientContactEmail(),client.getClientContactPhone(),client.getPanNumber(),client.getClientId()});
    }

    protected int deleteStatement(Client client)
    {
        String sql = "DELETE FROM t_payrl_client_mstr WHERE CLIENT_ID=?";
        return jdbcTemplate.update(sql,new Object[]{client.getClientId()});
    }
}
