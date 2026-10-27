package thaumichorizons.legacy;

import java.util.Random;

public class NoiseGeneratorImproved extends NoiseGenerator {
   private int[] permutations = new int[512];
   public double xCoord;
   public double yCoord;
   public double zCoord;
   private static final double[] field_152381_e = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
   private static final double[] field_152382_f = new double[]{1.0, 1.0, -1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0};
   private static final double[] field_152383_g = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};
   private static final double[] field_152384_h = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
   private static final double[] field_152385_i = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};

   public NoiseGeneratorImproved() {
      this(new Random());
   }

   public NoiseGeneratorImproved(Random var1) {
      this.xCoord = var1.nextDouble() * 256.0;
      this.yCoord = var1.nextDouble() * 256.0;
      this.zCoord = var1.nextDouble() * 256.0;
      int var2 = 0;

      while (var2 < 256) {
         this.permutations[var2] = var2++;
      }

      for (int var5 = 0; var5 < 256; var5++) {
         int var3 = var1.nextInt(256 - var5) + var5;
         int var4 = this.permutations[var5];
         this.permutations[var5] = this.permutations[var3];
         this.permutations[var3] = var4;
         this.permutations[var5 + 256] = this.permutations[var5];
      }
   }

   public final double lerp(double var1, double var3, double var5) {
      return var3 + var1 * (var5 - var3);
   }

   public final double func_76309_a(int var1, double var2, double var4) {
      int var6 = var1 & 15;
      return field_152384_h[var6] * var2 + field_152385_i[var6] * var4;
   }

   public final double grad(int var1, double var2, double var4, double var6) {
      int var8 = var1 & 15;
      return field_152381_e[var8] * var2 + field_152382_f[var8] * var4 + field_152383_g[var8] * var6;
   }

   public void populateNoiseArray(
      double[] var1, double var2, double var4, double var6, int var8, int var9, int var10, double var11, double var13, double var15, double var17
   ) {
      if (var9 == 1) {
         int var64 = 0;
         int var66 = 0;
         int var21 = 0;
         int var69 = 0;
         double var72 = 0.0;
         double var76 = 0.0;
         int var80 = 0;
         double var82 = 1.0 / var17;

         for (int var30 = 0; var30 < var8; var30++) {
            double var83 = var2 + var30 * var11 + this.xCoord;
            int var85 = (int)var83;
            if (var83 < var85) {
               var85--;
            }

            int var34 = var85 & 0xFF;
            var83 -= var85;
            double var86 = var83 * var83 * var83 * (var83 * (var83 * 6.0 - 15.0) + 10.0);

            for (int var87 = 0; var87 < var10; var87++) {
               double var89 = var6 + var87 * var15 + this.zCoord;
               int var91 = (int)var89;
               if (var89 < var91) {
                  var91--;
               }

               int var92 = var91 & 0xFF;
               var89 -= var91;
               double var93 = var89 * var89 * var89 * (var89 * (var89 * 6.0 - 15.0) + 10.0);
               var64 = this.permutations[var34] + 0;
               var66 = this.permutations[var64] + var92;
               var21 = this.permutations[var34 + 1] + 0;
               var69 = this.permutations[var21] + var92;
               var72 = this.lerp(var86, this.func_76309_a(this.permutations[var66], var83, var89), this.grad(this.permutations[var69], var83 - 1.0, 0.0, var89));
               var76 = this.lerp(
                  var86,
                  this.grad(this.permutations[var66 + 1], var83, 0.0, var89 - 1.0),
                  this.grad(this.permutations[var69 + 1], var83 - 1.0, 0.0, var89 - 1.0)
               );
               double var94 = this.lerp(var93, var72, var76);
               var1[var80++] += var94 * var82;
            }
         }
      } else {
         int var19 = 0;
         double var20 = 1.0 / var17;
         int var22 = -1;
         int var23 = 0;
         int var24 = 0;
         int var25 = 0;
         int var26 = 0;
         int var27 = 0;
         int var28 = 0;
         double var29 = 0.0;
         double var31 = 0.0;
         double var33 = 0.0;
         double var35 = 0.0;

         for (int var37 = 0; var37 < var8; var37++) {
            double var38 = var2 + var37 * var11 + this.xCoord;
            int var40 = (int)var38;
            if (var38 < var40) {
               var40--;
            }

            int var41 = var40 & 0xFF;
            var38 -= var40;
            double var42 = var38 * var38 * var38 * (var38 * (var38 * 6.0 - 15.0) + 10.0);

            for (int var44 = 0; var44 < var10; var44++) {
               double var45 = var6 + var44 * var15 + this.zCoord;
               int var47 = (int)var45;
               if (var45 < var47) {
                  var47--;
               }

               int var48 = var47 & 0xFF;
               var45 -= var47;
               double var49 = var45 * var45 * var45 * (var45 * (var45 * 6.0 - 15.0) + 10.0);

               for (int var51 = 0; var51 < var9; var51++) {
                  double var52 = var4 + var51 * var13 + this.yCoord;
                  int var54 = (int)var52;
                  if (var52 < var54) {
                     var54--;
                  }

                  int var55 = var54 & 0xFF;
                  var52 -= var54;
                  double var56 = var52 * var52 * var52 * (var52 * (var52 * 6.0 - 15.0) + 10.0);
                  if (var51 == 0 || var55 != var22) {
                     var22 = var55;
                     var23 = this.permutations[var41] + var55;
                     var24 = this.permutations[var23] + var48;
                     var25 = this.permutations[var23 + 1] + var48;
                     var26 = this.permutations[var41 + 1] + var55;
                     var27 = this.permutations[var26] + var48;
                     var28 = this.permutations[var26 + 1] + var48;
                     var29 = this.lerp(
                        var42, this.grad(this.permutations[var24], var38, var52, var45), this.grad(this.permutations[var27], var38 - 1.0, var52, var45)
                     );
                     var31 = this.lerp(
                        var42,
                        this.grad(this.permutations[var25], var38, var52 - 1.0, var45),
                        this.grad(this.permutations[var28], var38 - 1.0, var52 - 1.0, var45)
                     );
                     var33 = this.lerp(
                        var42,
                        this.grad(this.permutations[var24 + 1], var38, var52, var45 - 1.0),
                        this.grad(this.permutations[var27 + 1], var38 - 1.0, var52, var45 - 1.0)
                     );
                     var35 = this.lerp(
                        var42,
                        this.grad(this.permutations[var25 + 1], var38, var52 - 1.0, var45 - 1.0),
                        this.grad(this.permutations[var28 + 1], var38 - 1.0, var52 - 1.0, var45 - 1.0)
                     );
                  }

                  double var58 = this.lerp(var56, var29, var31);
                  double var60 = this.lerp(var56, var33, var35);
                  double var62 = this.lerp(var49, var58, var60);
                  var1[var19++] += var62 * var20;
               }
            }
         }
      }
   }
}
