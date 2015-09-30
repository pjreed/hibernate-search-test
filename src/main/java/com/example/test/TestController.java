package com.example.test;

import com.example.account.Account;
import com.example.account.AccountRepository;
import com.example.account.PhoneNumber;
import com.example.account.UserService;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Controller
@RequestMapping("test")
public class TestController {
    @Autowired
    private AccountRepository accountRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Inject
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(TestController.class);

    @Transactional
    public Account createAccount(String username, String password, String role, String pnPrefix) {
        Account account = new Account(username, password, role);
        account.getPhoneNumbers().add(new PhoneNumber(account, pnPrefix + "-1111"));
        account.getPhoneNumbers().add(new PhoneNumber(account, pnPrefix + "-1112"));
        account.getPhoneNumbers().add(new PhoneNumber(account, pnPrefix + "-1113"));
        return accountRepository.save(account);
    }

    @RequestMapping("/addusers")
    public String addusers() {
        createAccount("user", "demo", "ROLE_USER", "555");
        createAccount("admin", "admin", "ROLE_ADMIN", "556");

        return "test/addusers";
    }

    @RequestMapping("/reindex")
    public String reindex() {
        logger.info("Beginning reindexing.");
        FullTextEntityManager ftem = Search.getFullTextEntityManager(entityManager);

        try {
            ftem.createIndexer().startAndWait();
            ftem.flushToIndexes();
        } catch (InterruptedException e) {
            logger.error("Interrupted while reindexing.", e);
        }

        logger.info("Done reindexing.");

        return "search/reindex";
    }
}
