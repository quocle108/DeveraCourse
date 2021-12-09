package devera.score.example;

import score.Address;
import java.math.BigInteger;
import java.util.List;
import score.ObjectReader;
import score.ObjectWriter;
import scorex.util.ArrayList;
import java.util.Map;

public class Withdrawal {
  private final BigInteger amount;
  private List<Address> approvers;
  private BigInteger approvedWeight;
  private String description;
  public Withdrawal(BigInteger amount, BigInteger approvedWeight, String description, List<Address> approvers) {
      this.amount = amount;
      this.approvers = approvers;
      this.approvedWeight = approvedWeight;
      this.description = description;
  }

  public List<Address> getApprovers() {
      return this.approvers;
  }

  public BigInteger getApprovedWeight() {
      return this.approvedWeight;
  }

  public BigInteger getAmount() {
      return this.amount;
  }

  public String getDescription() {
    return this.description;
  }

  public void vote(Address voter, BigInteger weight) {
    this.approvedWeight = this.approvedWeight.add(weight);
    this.approvers.add(voter);
  }

  public Map<String, Object> toMap() {
    return Map.of(
            "_amount", this.amount.toString(),
            "_approvedWeight", this.approvedWeight.toString(),
            "_description", this.description,
            "_approvers", this.approvers
    );
  }

  public static void writeObject(ObjectWriter w, Withdrawal v) {
    w.beginList(3);
    w.write(v.getAmount());
    w.write(v.getApprovedWeight());
    w.write(v.getDescription());
        List<Address> approvers = v.getApprovers();
        w.beginList(approvers.size());

        for(int i = 0; i < approvers.size(); i++) {
            w.write(approvers.get(i));
        }
        w.end();
    w.end();
  }

  public static Withdrawal readObject(ObjectReader r) {
    r.beginList();
    BigInteger amount = r.readBigInteger();
    BigInteger approvedWeight = r.readBigInteger();
    String description = r.readString();
    List<Address> approvers = new ArrayList<Address>(150);
        r.beginList();
        while (r.hasNext()) {
            Address v = r.readAddress();
            approvers.add(v);
        }
    r.end();
    
    return new Withdrawal(amount, approvedWeight, description, approvers);
  }
}
