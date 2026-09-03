package com.unsiiyat.backend.common.security;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.unsiiyat.backend.common.exceptions.ValidationException;

/**
 * Symmetric encryption for secrets stored at rest (currently per-school
 * Razorpay key/webhook
 * secrets). AES-256-GCM with a single deployment-level master key supplied via
 * {@code app.encryption.key} (base64 of 32 raw bytes). The key never lives in
 * the DB; the DB
 * only holds ciphertext. Output format is base64( 12-byte IV || ciphertext+tag
 * ).
 *
 * <p>
 * The app boots fine without a key configured — encryption is only exercised
 * when a school
 * saves payment credentials, and a clear error is raised then if the key is
 * missing/invalid.
 */
@Service
public class CryptoService {

    private static final int IV_LENGTH = 12; // GCM standard nonce size
    private static final int TAG_LENGTH_BITS = 128;
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private final byte[] key;
    private final SecureRandom random = new SecureRandom();

    public CryptoService(@Value("${app.encryption.key:}") String base64Key) {
        byte[] decoded = null;
        if (base64Key != null && !base64Key.isBlank()) {
            try {
                decoded = Base64.getDecoder().decode(base64Key.trim());
            } catch (IllegalArgumentException e) {
                decoded = null;
            }
        }
        // Only 16/24/32-byte keys are valid AES keys; anything else is treated as "not
        // configured" so the failure surfaces clearly at use-time, not as a boot crash.
        this.key = (decoded != null && (decoded.length == 16 || decoded.length == 24 || decoded.length == 32))
                ? decoded
                : null;
    }

    public boolean isConfigured() {
        return key != null;
    }

    public String encrypt(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        requireKey();
        try {
            byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"),
                    new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] ct = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] out = ByteBuffer.allocate(iv.length + ct.length).put(iv).put(ct).array();
            return Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            throw new ValidationException("Failed to encrypt the value.");
        }
    }

    public String decrypt(String ciphertextBase64) {
        if (ciphertextBase64 == null) {
            return null;
        }
        requireKey();
        try {
            byte[] all = Base64.getDecoder().decode(ciphertextBase64);
            ByteBuffer buf = ByteBuffer.wrap(all);
            byte[] iv = new byte[IV_LENGTH];
            buf.get(iv);
            byte[] ct = new byte[buf.remaining()];
            buf.get(ct);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"),
                    new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            return new String(cipher.doFinal(ct), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new ValidationException("Failed to decrypt a stored secret.");
        }
    }

    private void requireKey() {
        if (key == null) {
            throw new ValidationException(
                    "Encryption is not configured on this deployment (app.encryption.key). "
                            + "Payment credentials cannot be stored until it is set.");
        }
    }
}
