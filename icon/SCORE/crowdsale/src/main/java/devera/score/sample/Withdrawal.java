package devera.score.example;

import java.math.BigInteger;
import java.util.List;
import score.ObjectReader;
import score.ObjectWriter;
import scorex.util.ArrayList;

public class Withdrawal {
  private final BigInteger amount;
  private final List<byte[]> approvers;
  private final BigInteger approvedWeight;
  public Validators(BigInteger amount, List<byte[]> approvers, BigInteger approvedWeight) {
      this.amount = amount;
      this.approvers = approvers;
      this.approvedWeight = approvedWeight;
  }

  public List<byte[]> getApprovers() {
      return this.approvers;
  }

  public BigInteger approvedWeight() {
      return this.approvedWeight;
  }

  public BigInteger amount() {
      return this.amount;
  }

  public static void writeObject(ObjectWriter w, Validators v) {
      List<byte[]> validators = v.get();
      w.beginList(validators.size());

      for(int i = 0; i < validators.size(); i++) {
          w.write(validators.get(i));
      }

      w.end();
  }

  public static Validators readObject(ObjectReader r) {
      r.beginList();
      List<byte[]> validators = new ArrayList<byte[]>(150);
      while (r.hasNext()) {
          byte[] v = r.readByteArray();
          validators.add(v);
      }
      r.end();

      return new Validators(validators);
  }
}
