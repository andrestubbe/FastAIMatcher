package fastaimatcher;

import fastfileformat.BinaryHeader;
import fastfileformat.BinaryReader;
import fastfileformat.BinaryWriter;
import fastfileformat.FastFileFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * High-speed binary serializer and stream decoder for SOX Audit and Compliance Match Reports (.matchbin).
 * Built on top of FastFileFormat and FastBinary VarInt compression.
 */
public final class MatcherCodec {
    /**
     * Payload type identifier for FastJava Compliance Match Reports (0x0007).
     */
    public static final short PAYLOAD_TYPE_MATCHBIN = 0x0007;

    private MatcherCodec() {}

    /**
     * Encodes a list of compliance findings into a compressed FastFileFormat binary byte array.
     */
    public static byte[] encode(List<MatchFinding> findings) {
        if (findings == null || findings.isEmpty()) {
            BinaryWriter finalWriter = FastFileFormat.binaryWriter(12);
            finalWriter.writeHeader(FastFileFormat.DEFAULT_MAGIC, FastFileFormat.DEFAULT_VERSION, PAYLOAD_TYPE_MATCHBIN, 0);
            return finalWriter.toByteArray();
        }

        BinaryWriter payloadWriter = FastFileFormat.binaryWriter(findings.size() * 64);
        payloadWriter.writeVarInt(findings.size());

        for (MatchFinding f : findings) {
            payloadWriter.writeString(f.ruleId() != null ? f.ruleId() : "");
            payloadWriter.writeByte((byte) f.status().ordinal());
            payloadWriter.writeFloat(f.confidenceScore());
            payloadWriter.writeString(f.explanation() != null ? f.explanation() : "");
            payloadWriter.writeString(f.evidenceSnippet() != null ? f.evidenceSnippet() : "");
        }

        byte[] payload = payloadWriter.toByteArray();

        BinaryWriter finalWriter = FastFileFormat.binaryWriter(12 + payload.length);
        finalWriter.writeHeader(
                FastFileFormat.DEFAULT_MAGIC,
                FastFileFormat.DEFAULT_VERSION,
                PAYLOAD_TYPE_MATCHBIN,
                payload.length
        );
        finalWriter.writeBytes(payload);
        return finalWriter.toByteArray();
    }

    /**
     * Decodes a .matchbin binary payload into a list of MatchFinding instances.
     */
    public static List<MatchFinding> decode(byte[] bytes) {
        if (bytes == null || bytes.length < 12) {
            return Collections.emptyList();
        }

        BinaryReader reader = FastFileFormat.binaryReader(bytes);
        BinaryHeader header = reader.readHeader();

        if (header.getMagic() != FastFileFormat.DEFAULT_MAGIC) {
            throw new IllegalArgumentException("Invalid FastFileFormat magic header: " + Integer.toHexString(header.getMagic()));
        }
        if (header.getPayloadType() != PAYLOAD_TYPE_MATCHBIN) {
            throw new IllegalArgumentException("Unexpected payload type for Matchbin: " + header.getPayloadType());
        }
        if (header.getPayloadLength() == 0) {
            return Collections.emptyList();
        }

        int count = reader.readVarInt();
        List<MatchFinding> list = new ArrayList<>(count);
        MatchFinding.Status[] statuses = MatchFinding.Status.values();

        for (int i = 0; i < count; i++) {
            String ruleId = reader.readString();
            int statusIdx = reader.readByte() & 0xFF;
            MatchFinding.Status status = (statusIdx < statuses.length) ? statuses[statusIdx] : MatchFinding.Status.WARNING;
            float conf = reader.readFloat();
            String explanation = reader.readString();
            String snippet = reader.readString();

            list.add(new MatchFinding(ruleId, status, conf, explanation, snippet));
        }
        return Collections.unmodifiableList(list);
    }

    public static void writeToFile(Path path, List<MatchFinding> findings) throws IOException {
        byte[] bytes = encode(findings);
        Files.write(path, bytes);
    }

    public static List<MatchFinding> readFromFile(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return decode(bytes);
    }
}
