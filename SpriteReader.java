import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class SpriteReader {
    int tileWidth;
    int tileHeight;
    BufferedImage spriteSheet;
    List<ImageIcon> extractedIcons = new ArrayList<>();
    public SpriteReader(String filepath, int tileWidth, int tileHeight) throws IOException {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.spriteSheet = ImageIO.read(new File(filepath));
        extractTile();

    }

    private void extractTile() {
        int sheetWidth = spriteSheet.getWidth();
        int sheetHeight = spriteSheet.getHeight();
//        List<ImageIcon> icons = new ArrayList<>();
        for (int y = 0; y < sheetHeight / tileHeight; y++) {
            for (int x = 0; x < sheetWidth / tileWidth; x++) {
                // Extract each tile
                BufferedImage tile = spriteSheet.getSubimage(
                        x * tileWidth, y * tileHeight, tileWidth, tileHeight
                );

                ImageIcon tileIcon = new ImageIcon(tile);
                extractedIcons.add(tileIcon);
            }
        }
    }

    public List<ImageIcon> getSpriteSheetIcons() {
        return extractedIcons;
    }


    public static void main(String[] args) {
        try {
            // Load the sprite sheet
            BufferedImage spriteSheet = ImageIO.read(new File("bomb-proximity-count.png"));

            // Define tile dimensions (adjust if tiles are not 16x16)
            int tileWidth = 16;  // Width of each tile
            int tileHeight = 16; // Height of each tile

            // Create a JFrame to display the tiles
            JFrame frame = new JFrame("Sprite Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new GridLayout(9, 9)); // Adjust grid size for preview
            frame.setSize(2400, 2400);

            // Iterate through the sprite sheet
            int sheetWidth = spriteSheet.getWidth();
            int sheetHeight = spriteSheet.getHeight();

            for (int y = 0; y < sheetHeight / tileHeight; y++) {
                for (int x = 0; x < sheetWidth / tileWidth; x++) {
                    // Extract each tile
                    BufferedImage tile = spriteSheet.getSubimage(
                            x * tileWidth, y * tileHeight, tileWidth, tileHeight
                    );

                    // Convert to ImageIcon and display in JButton
                    ImageIcon tileIcon = new ImageIcon(tile);
                    JButton button = new JButton(tileIcon);
                    button.setPreferredSize(new Dimension(tileWidth, tileHeight));

                    // Add action listener to identify tile
                    int finalX = x;
                    int finalY = y;
                    button.addActionListener(e -> {
                        System.out.println("Tile clicked at row " + finalY + ", col " + finalX);
                    });

                    frame.add(button);
                }
            }

            // Show the frame
            frame.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
