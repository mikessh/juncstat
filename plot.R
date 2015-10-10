library(ggplot2); library(plyr)

args <- commandArgs()

df <- read.table(args[1], header=T)
df <- subset(df, mappings>0)

df.g <- ddply(df, .(condition, gene), summarize, 
              score = sum(score),
              oof.mappings = sum(oof.mappings),
              mappings = sum(mappings))

df.g$oof.freq <- (df.g$score/sum(df.g$score)) *     # junction freq
                 (df.g$oof.mappings/df.g$mappings)  # ratio of oof mappings

pdf(args[2])
ggplot(df.g, aes(x=oof.freq,color=condition))+stat_ecdf()+scale_x_log10()+scale_y_log10()+scale_color_brewer(palette="Set2")
dev.off()

#ks.test(subset(df.g, condition == "control")$oof.freq, subset(df.g, condition == "upf1-2")$oof.freq)