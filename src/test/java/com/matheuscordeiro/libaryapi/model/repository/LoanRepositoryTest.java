package com.matheuscordeiro.libaryapi.model.repository;

import com.matheuscordeiro.libaryapi.model.entity.Book;
import com.matheuscordeiro.libaryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static com.matheuscordeiro.libaryapi.model.repository.BookRepositoryTest.createNewBook;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {
    @Autowired
    LoanRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("Must should check if the borrowed book has been returned")
    public void existsByBookAndNotReturnedTest() {
        Loan loan = createAndPersistLoan(LocalDate.now());
        Book book = loan.getBook();
        boolean exists = repository.existsByBookAndNotReturned(book);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Must find by isbn of the book or customer")
    public void findByBookIsbnOrCustomerTest(){
        Loan loan = createAndPersistLoan(LocalDate.now());
        Page<Loan> result = repository.findByBookIsbnOrCustomer("123", "Junior", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    public Loan createAndPersistLoan(LocalDate loanDate){
        Book book = createNewBook("123");
        entityManager.persist(book);
        Loan loan = Loan.builder().book(book).customer("Junior").loanDate(loanDate).build();
        entityManager.persist(loan);
        return loan;
    }
}
