package edu.stanford.cs.crypto.efficientct.innerproduct;

import cyclops.collections.immutable.VectorX;
import edu.stanford.cs.crypto.efficientct.VerificationFailedException;
import edu.stanford.cs.crypto.efficientct.circuit.groups.BN128Group;
import edu.stanford.cs.crypto.efficientct.circuit.groups.BouncyCastleECPoint;
import edu.stanford.cs.crypto.efficientct.circuit.groups.Group;
import edu.stanford.cs.crypto.efficientct.circuit.groups.Secp256k1;
import edu.stanford.cs.crypto.efficientct.linearalgebra.FieldVector;
import edu.stanford.cs.crypto.efficientct.linearalgebra.VectorBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by buenz on 6/29/17.
 */
@RunWith(Parameterized.class)

public class EfficientInnerProductVerifierTest {
    @Parameterized.Parameters
    public static Object[] data() {
        return new Object[]{new Secp256k1(), new BN128Group()};
    }

    private final Group<BouncyCastleECPoint> curve;

    private final BigInteger q;

    public EfficientInnerProductVerifierTest(Group<BouncyCastleECPoint> curve) {
        this.curve = curve;
        this.q = curve.groupOrder();
    }

    @Test
    public void testCompletness() throws Exception {
        InnerProductProofSystem<BouncyCastleECPoint> system = new InnerProductProofSystem<>();
        VectorBase<BouncyCastleECPoint> parameters = system.generatePublicParams(16, curve);
        Random random = new Random();

        FieldVector as = FieldVector.from(VectorX.generate(16, () -> new BigInteger(4, random)).materialize(), q);
        FieldVector bs = FieldVector.from(VectorX.generate(16, () -> new BigInteger(4, random)).materialize(), q);
        BigInteger c = as.innerPoduct(bs);
        BouncyCastleECPoint vTot = parameters.commit(as, bs, c);
        System.out.println(vTot.stringRepresentation());
        InnerProductWitness witness = new InnerProductWitness(as, bs);
        InnerProductProver<BouncyCastleECPoint> prover = system.getProver();
        System.out.println(as);
        System.out.println(bs);
        System.out.println(c);
        InnerProductProof<BouncyCastleECPoint> proof = prover.generateProof(parameters, vTot, witness);
        EfficientInnerProductVerifier<BouncyCastleECPoint> verifier = new EfficientInnerProductVerifier<>();
        verifier.verify(parameters, vTot, proof);

    }

    @Test(expected = VerificationFailedException.class)
    public void testSoundness() throws Exception {
        InnerProductProofSystem<BouncyCastleECPoint> system = new InnerProductProofSystem<>();
        VectorBase<BouncyCastleECPoint> parameters = system.generatePublicParams(16, curve);
        Random random = new Random();

        FieldVector as = FieldVector.from(random.ints(16, 0, 20).mapToObj(BigInteger::valueOf).collect(Collectors.toList()), q);
        FieldVector bs = FieldVector.from(random.ints(16, 0, 20).mapToObj(BigInteger::valueOf).collect(Collectors.toList()), q);
        System.out.println(as);
        System.out.println(bs);
        BigInteger c = as.innerPoduct(bs).add(BigInteger.ONE);
        BouncyCastleECPoint vTot = parameters.commit(as, bs, c);
        System.out.println(vTot.stringRepresentation());
        InnerProductWitness witness = new InnerProductWitness(as, bs);
        InnerProductProver<BouncyCastleECPoint> prover = system.getProver();
        System.out.println(as);
        System.out.println(bs);
        System.out.println(c);
        InnerProductProof<BouncyCastleECPoint> proof = prover.generateProof(parameters, vTot, witness);
        EfficientInnerProductVerifier<BouncyCastleECPoint> verifier = new EfficientInnerProductVerifier<>();
        verifier.verify(parameters, vTot, proof);

    }

    @Test
    public void testVerifier1() throws Exception {
        InnerProductProofSystem<BouncyCastleECPoint> system = new InnerProductProofSystem<>();
        VectorBase<BouncyCastleECPoint> parameters = system.generatePublicParams(1024, curve);

        FieldVector as = FieldVector.random(1024, q);
        FieldVector bs = FieldVector.random(1024, q);
        BigInteger c = as.innerPoduct(bs);
        BouncyCastleECPoint vTot = parameters.commit(as, bs, c);
        InnerProductWitness witness = new InnerProductWitness(as, bs);
        InnerProductProver<BouncyCastleECPoint> prover = system.getProver();
        InnerProductProof<BouncyCastleECPoint> proof = prover.generateProof(parameters, vTot, witness);
        EfficientInnerProductVerifier<BouncyCastleECPoint> verifier = new EfficientInnerProductVerifier<>();
        verifier.verify(parameters, vTot, proof);

    }

    @Test
    public void testVerifier2() throws Exception {
        InnerProductProofSystem<BouncyCastleECPoint> system = new InnerProductProofSystem<>();
        VectorBase<BouncyCastleECPoint> parameters = system.generatePublicParams(1024, curve);

        FieldVector as = FieldVector.random(1024, q);
        FieldVector bs = FieldVector.random(1024, q);
        BigInteger c = as.innerPoduct(bs);
        BouncyCastleECPoint vTot = parameters.commit(as, bs, c);
        InnerProductWitness witness = new InnerProductWitness(as, bs);
        InnerProductProver<BouncyCastleECPoint> prover = system.getProver();
        InnerProductProof<BouncyCastleECPoint> proof = prover.generateProof(parameters, vTot, witness);
        InnerProductVerifier<BouncyCastleECPoint> verifier = new InnerProductVerifier<>();
        verifier.verify(parameters, vTot, proof);

    }

}
