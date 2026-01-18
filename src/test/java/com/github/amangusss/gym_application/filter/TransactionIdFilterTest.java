package com.github.amangusss.gym_application.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.slf4j.MDC;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionIdFilter Tests")
class TransactionIdFilterTest {

    private static final String TRANSACTION_ID_HEADER = "X-transaction-id";
    private static final String EXISTING_TRANSACTION_ID = "existing-transaction-id-123";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private TransactionIdFilter transactionIdFilter;

    @BeforeEach
    void setUp() {
        transactionIdFilter = new TransactionIdFilter();
        MDC.clear();
    }

    @Nested
    @DisplayName("Transaction ID Generation Tests")
    class TransactionIdGenerationTests {

        @Test
        @DisplayName("Should use existing transaction ID from header")
        void shouldUseExistingTransactionIdFromHeader() throws ServletException, IOException {
            when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn(EXISTING_TRANSACTION_ID);

            transactionIdFilter.doFilterInternal(request, response, filterChain);

            verify(response).setHeader(eq(TRANSACTION_ID_HEADER), eq(EXISTING_TRANSACTION_ID));
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should generate new transaction ID when header is missing")
        void shouldGenerateNewTransactionIdWhenHeaderMissing() throws ServletException, IOException {
            when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn(null);
            ArgumentCaptor<String> transactionIdCaptor = ArgumentCaptor.forClass(String.class);

            transactionIdFilter.doFilterInternal(request, response, filterChain);

            verify(response).setHeader(eq(TRANSACTION_ID_HEADER), transactionIdCaptor.capture());
            assertThat(transactionIdCaptor.getValue()).isNotNull();
            assertThat(transactionIdCaptor.getValue()).isNotEmpty();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Should generate new transaction ID when header is blank")
        void shouldGenerateNewTransactionIdWhenHeaderBlank() throws ServletException, IOException {
            when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn("   ");
            ArgumentCaptor<String> transactionIdCaptor = ArgumentCaptor.forClass(String.class);

            transactionIdFilter.doFilterInternal(request, response, filterChain);

            verify(response).setHeader(eq(TRANSACTION_ID_HEADER), transactionIdCaptor.capture());
            assertThat(transactionIdCaptor.getValue()).isNotBlank();
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("MDC Cleanup Tests")
    class MdcCleanupTests {

        @Test
        @DisplayName("Should clean MDC after filter chain execution")
        void shouldCleanMdcAfterFilterChain() throws ServletException, IOException {
            when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn(EXISTING_TRANSACTION_ID);

            transactionIdFilter.doFilterInternal(request, response, filterChain);

            assertThat(MDC.get("transactionId")).isNull();
        }

        @Test
        @DisplayName("Should clean MDC even when exception occurs")
        void shouldCleanMdcEvenWhenExceptionOccurs() throws ServletException, IOException {
            when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn(EXISTING_TRANSACTION_ID);
            RuntimeException expectedException = new RuntimeException("Test exception");

            doThrow(expectedException).when(filterChain).doFilter(request, response);

            try {
                transactionIdFilter.doFilterInternal(request, response, filterChain);
            } catch (RuntimeException e) {
                assertThat(e).isEqualTo(expectedException);
            }

            assertThat(MDC.get("transactionId")).isNull();
        }
    }
}
