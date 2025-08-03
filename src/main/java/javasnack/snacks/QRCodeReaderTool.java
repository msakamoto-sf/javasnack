package javasnack.snacks;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import javasnack.RunnableSnack;
import javasnack.tool.ConsoleUtil;

public class QRCodeReaderTool implements RunnableSnack {
    @Override
    public void run(final String... args) {
        final String filepath = ConsoleUtil.readLine("QR Code Image file path: ");
        final String csname = ConsoleUtil.readLine("charset(if enter, use UTF-8 as default): ");
        final String preferedCharsetName = (Objects.isNull(csname) || csname.trim().length() == 0)
            ? StandardCharsets.UTF_8.name()
            : csname;
        try {
            // windows explorer からの file path コピペで "" 囲みになるのを自動除去
            final BufferedImage image = ImageIO.read(new File(filepath.replace("\"", "")));
            final LuminanceSource source = new BufferedImageLuminanceSource(image);
            final Binarizer bin = new HybridBinarizer(source);
            final BinaryBitmap bitmap = new BinaryBitmap(bin);
            final QRCodeReader reader = new QRCodeReader();
            final Map<DecodeHintType, ?> decodeHints = Map.of(
                DecodeHintType.CHARACTER_SET,
                Charset.forName(preferedCharsetName));
            final Result result = reader.decode(bitmap, decodeHints);
            System.out.print("result:[");
            System.out.print(result.getText());
            System.out.println("]");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
