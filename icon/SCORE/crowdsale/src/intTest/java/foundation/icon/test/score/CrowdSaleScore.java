/*
 * Copyright 2018 ICON Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package foundation.icon.test.score;

import foundation.icon.icx.Wallet;
import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.IconAmount;
import foundation.icon.icx.data.TransactionResult;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import foundation.icon.test.Constants;
import foundation.icon.test.ResultTimeoutException;
import foundation.icon.test.TransactionFailureException;
import foundation.icon.test.TransactionHandler;

import java.io.IOException;
import java.math.BigInteger;

import static foundation.icon.test.Env.LOG;

public class CrowdSaleScore extends Score {

    public static CrowdSaleScore mustDeploy(TransactionHandler txHandler, Wallet wallet,
                                            Address tokenAddress, BigInteger fundingGoalInIcx, BigInteger durationInBlocks, BigInteger tokenPrice)
            throws ResultTimeoutException, TransactionFailureException, IOException {
        LOG.infoEntering("deploy", "Crowdsale");
        RpcObject params = new RpcObject.Builder()
                .put("_fundingGoalInIcx", new RpcValue(fundingGoalInIcx))
                .put("_tokenScore", new RpcValue(tokenAddress))
                .put("_durationInBlocks", new RpcValue(durationInBlocks))
                .put("_tokenPrice", new RpcValue(BigInteger.valueOf(10)))
                .build();
        Score score = txHandler.deploy(wallet, getFilePath("crowdsale"), params);
        LOG.info("scoreAddr = " + score.getAddress());
        LOG.infoExiting();
        return new CrowdSaleScore(score);
    }

    public CrowdSaleScore(Score other) {
        super(other);
    }

    public TransactionResult withdraw(Wallet wallet, BigInteger value, String description)
            throws ResultTimeoutException, IOException {
        RpcObject params = new RpcObject.Builder()
            .put("_value", new RpcValue(value))
            .put("_description", new RpcValue(description))
            .build();
        return invokeAndWaitResult(wallet, "withdraw", params);
    }

    public TransactionResult executeWithdrawal(Wallet wallet)
        throws ResultTimeoutException, IOException {
        RpcObject params = new RpcObject.Builder()
            .build();
        return invokeAndWaitResult(wallet, "executeWithdrawal", params);
    }

    public TransactionResult voteWithdrawal(Wallet wallet)
    throws ResultTimeoutException, IOException {
        RpcObject params = new RpcObject.Builder()
            .build();
        return invokeAndWaitResult(wallet, "voteWithdrawal", params);
    }

    public BigInteger amountRaised()
            throws ResultTimeoutException, IOException {
        return this.call("amountRaised", null).asInteger();
    }

    public void ensureWithdrawal(BigInteger checkingAmount, BigInteger checkingWeight, String checkingDescription)
        throws IOException {
        RpcObject withdrawal = this.call("withdrawal", null).asObject();
        BigInteger amount = new BigInteger(withdrawal.getItem("_amount").toString());
        BigInteger weight = new BigInteger(withdrawal.getItem("_approvedWeight").toString());
        String description = withdrawal.getItem("_description").asString();

        if (!checkingAmount.equals(amount)) {
            throw new IOException("withdrawal amount is invalid");
        }

        if (!checkingWeight.equals(weight)) {
            throw new IOException("withdrawal amount is invalid");
        }

        if (!checkingDescription.equals(description)) {
            throw new IOException("withdrawal description is invalid");
        }
    }

    public void ensureFundingGoal(TransactionResult result, BigInteger fundingGoalInIcx)
            throws IOException {
        TransactionResult.EventLog event = findEventLog(result, getAddress(), "CrowdsaleStarted(int,int)");
        if (event != null) {
            BigInteger fundingGoalInLoop = IconAmount.of(fundingGoalInIcx, IconAmount.Unit.ICX).toLoop();
            BigInteger fundingGoalFromScore = event.getData().get(0).asInteger();
            if (fundingGoalInLoop.equals(fundingGoalFromScore)) {
                return; // ensured
            }
        }
        throw new IOException("ensureFundingGoal failed.");
    }

    public void ensureFundDeposit(TransactionResult result, Address backer, BigInteger amount)
            throws IOException {
        TransactionResult.EventLog event = findEventLog(result, getAddress(), "FundDeposit(Address,int)");
        if (event != null) {
            Address _backer = event.getIndexed().get(1).asAddress();
            BigInteger _amount = event.getIndexed().get(2).asInteger();
            if (backer.equals(_backer) && amount.equals(_amount)) {
                return; // ensured
            }
        }
        throw new IOException("ensureFundDeposit failed.");
    }

    public void ensureFundWithdraw(TransactionResult result, Address backer, BigInteger amount)
            throws IOException {
        TransactionResult.EventLog event = findEventLog(result, getAddress(), "FundWithdraw(Address,int)");
        if (event != null) {
            Address _backer = event.getIndexed().get(1).asAddress();
            BigInteger _amount = event.getIndexed().get(2).asInteger();
            if (backer.equals(_backer) && amount.equals(_amount)) {
                return; // ensured
            }
        }
        throw new IOException("ensureFundDeposit failed.");
    }
}
