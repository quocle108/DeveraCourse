package devera.score.example;

import devera.score.token.irc2.IRC2BurnableToken;
import com.iconloop.score.test.Account;
import com.iconloop.score.test.Score;
import com.iconloop.score.test.ServiceManager;
import com.iconloop.score.test.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static java.math.BigInteger.TEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class CrowdsaleTest extends TestBase {
    // sample-token
    private static final String name = "MySampleToken";
    private static final String symbol = "MST";
    private static final int decimals = 18;
    private static final BigInteger initialSupply = BigInteger.valueOf(1000);
    private static final BigInteger totalSupply = initialSupply.multiply(TEN.pow(decimals));

    // sample-crowdsale
    private static final BigInteger fundingGoalInICX = BigInteger.valueOf(100);
    private static final BigInteger tokenPrice = BigInteger.valueOf(10);
    private static final BigInteger durationInBlocks = BigInteger.valueOf(32);

    private static final ServiceManager sm = getServiceManager();
    private static final Account owner = sm.createAccount();
    private Score tokenScore;
    private Score crowdsaleScore;

    private Crowdsale crowdsaleSpy;
    private final byte[] startCrowdsaleBytes = "start crowdsale".getBytes();

    @BeforeEach
    public void setup() throws Exception {
        // deploy token and crowdsale scores
        tokenScore = sm.deploy(owner, IRC2BurnableToken.class,
                name, symbol, decimals, initialSupply);
        crowdsaleScore = sm.deploy(owner, Crowdsale.class,
                fundingGoalInICX, tokenScore.getAddress(), durationInBlocks, tokenPrice);

        // setup spy object against the crowdsale object
        crowdsaleSpy = (Crowdsale) spy(crowdsaleScore.getInstance());
        crowdsaleScore.setInstance(crowdsaleSpy);
    }

    private void startCrowdsale() {
        // transfer all tokens to crowdsale score
        tokenScore.invoke(owner, "transfer", crowdsaleScore.getAddress(), totalSupply, startCrowdsaleBytes);
    }

    @Test
    void tokenFallback() {
        // transfer IRC2 token to start crowsale
        startCrowdsale();
        // verify
        verify(crowdsaleSpy).tokenFallback(owner.getAddress(), totalSupply, startCrowdsaleBytes);
        verify(crowdsaleSpy).CrowdsaleStarted(eq(ICX.multiply(fundingGoalInICX)), anyLong());
        assertEquals(totalSupply, tokenScore.call("balanceOf", crowdsaleScore.getAddress()));
    }

    @Test
    void fallback_crowdsaleNotYetStarted() {
        Account alice = sm.createAccount(100);
        BigInteger fund = ICX.multiply(BigInteger.valueOf(40));
        // crowdsale is not yet started
        assertThrows(AssertionError.class, () ->
                sm.transfer(alice, crowdsaleScore.getAddress(), fund));
    }

    @Test
    void fallback() {
        startCrowdsale();
        // fund 40 icx from Alice
        Account alice = sm.createAccount(100);
        BigInteger fund = ICX.multiply(BigInteger.valueOf(40));
        sm.transfer(alice, crowdsaleScore.getAddress(), fund);
        // verify
        verify(crowdsaleSpy).fallback();
        verify(crowdsaleSpy).FundDeposit(alice.getAddress(), fund);
        assertEquals(fund, Account.getAccount(crowdsaleScore.getAddress()).getBalance());
        assertTrue(fund.multiply(tokenPrice).equals(tokenScore.call("balanceOf", alice.getAddress())));
    }

    @Test
    void safeWithdrawal() {
        startCrowdsale();
        // fund 40 icx from Alice
        Account alice = sm.createAccount(100);
        sm.transfer(alice, crowdsaleScore.getAddress(), ICX.multiply(BigInteger.valueOf(40)));
        // fund 60 icx from Bob
        Account bob = sm.createAccount(100);
        sm.transfer(bob, crowdsaleScore.getAddress(), ICX.multiply(BigInteger.valueOf(60)));
        // make the goal reached
        sm.getBlock().increase(durationInBlocks.longValue());
        // invoke safeWithdrawal
        BigInteger withdrawAmount = ICX.multiply(BigInteger.valueOf(30));
        crowdsaleScore.invoke(owner, "withdraw", withdrawAmount);
        // verify
        verify(crowdsaleSpy).FundWithdraw(owner.getAddress(), withdrawAmount);
        assertEquals(withdrawAmount, Account.getAccount(owner.getAddress()).getBalance());
        assertEquals(ICX.multiply(fundingGoalInICX).subtract(withdrawAmount), crowdsaleScore.call("amountRaised"));
    }
}
