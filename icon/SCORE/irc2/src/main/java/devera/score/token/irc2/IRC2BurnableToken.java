package devera.score.token.irc2;

import score.Context;
import score.annotation.External;

import java.math.BigInteger;

public class IRC2BurnableToken extends IRC2Basic {
    public IRC2BurnableToken(String _name, String _symbol, int _decimals, BigInteger _initialSupply) {
        super(_name, _symbol, _decimals);

        // mint the initial token supply here
        Context.require(_initialSupply.compareTo(BigInteger.ZERO) >= 0);
        _mint(Context.getCaller(), _initialSupply.multiply(pow10(_decimals)));
    }

    /**
     * Destroys `_amount` tokens from the caller.
     */
    @External
    public void burn(BigInteger _amount) {
        _burn(Context.getCaller(), _amount);
    }

    private static BigInteger pow10(int exponent) {
        BigInteger result = BigInteger.ONE;
        for (int i = 0; i < exponent; i++) {
            result = result.multiply(BigInteger.TEN);
        }
        return result;
    }
}
