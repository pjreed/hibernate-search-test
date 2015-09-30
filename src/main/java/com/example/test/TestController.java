package com.example.test;

import com.example.account.Account;
import com.example.account.AccountRepository;
import com.example.account.PhoneNumber;
import com.example.account.UserService;
import com.google.common.collect.Lists;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        account.getPhoneNumbers().add(new PhoneNumber(account, pnPrefix + "555-1111"));
        account.getPhoneNumbers().add(new PhoneNumber(account, pnPrefix + "555-1112"));
        account.getPhoneNumbers().add(new PhoneNumber(account, pnPrefix + "555-1113"));
        return accountRepository.save(account);
    }

    @RequestMapping("/addusers")
    public String addusers() {
        if (accountRepository.count() == 0 ) {
            logger.info("Adding users.");
            for (int i = 0; i < 10; i++) {
                createAccount("user" + i, "password" + i, "ROLE_USER", Integer.toString(i) + "-");
            }

            logger.info("Done adding users.");
        }
        else {
            logger.info("Users already exist, not adding more.");
        }
        return "test/addusers";
    }

    @RequestMapping("/search")
    @Transactional
    @ResponseBody
    public List<Account> search(@RequestParam String number) {
        try {
            FullTextEntityManager ftem = Search.getFullTextEntityManager(entityManager);
            QueryBuilder builder = ftem.getSearchFactory().buildQueryBuilder().forEntity(Account.class).get();
            Query query = builder.keyword().wildcard().onFields(
                    "email", "role", "phone_number_number").matching(number).createQuery();
            FullTextQuery ftQuery = ftem.createFullTextQuery(query, Account.class);
            List results = ftQuery.getResultList();
            List<Account> accounts = Lists.newArrayList();

            // Sort the results so that they come out the same every time.
            for (Object obj : results) {
                if (obj instanceof Account) {
                    Account acct = (Account)obj;
                    Collections.sort(acct.getPhoneNumbers(), new Comparator<PhoneNumber>() {
                        @Override
                        public int compare(PhoneNumber p1, PhoneNumber p2) {
                            return p1.getNumber().compareTo(p2.getNumber());
                        }
                    });
                    accounts.add((Account)obj);
                }
            }
            Collections.sort(accounts, new Comparator<Account>() {
                @Override
                public int compare(Account a1, Account a2) {
                    return a1.getEmail().compareTo(a2.getEmail());
                }
            });

            return accounts;
        }
        catch (RuntimeException e) {
            logger.error("Unable to get results:", e);
            throw e;
        }
    }

    @RequestMapping("/reindex")
    public String reindex() {
        logger.info("Beginning reindexing.");
        FullTextEntityManager ftem = Search.getFullTextEntityManager(entityManager);

        try {
            ftem.createIndexer().startAndWait();
            ftem.flushToIndexes();
        }
        catch (InterruptedException e) {
            logger.error("Interrupted while reindexing.", e);
        }

        logger.info("Done reindexing.");

        return "test/reindex";
    }
}
