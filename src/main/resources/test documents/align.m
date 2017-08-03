function [S] = align
%ALIGN Summary of this function goes here
%   Detailed explanation goes here

seqs = fastaread('sequences.txt');
seqs = multialign(seqs);
[C,S] = seqconsensus(seqs,'limits',[0 330],'gaps','all');

fileID = fopen('aligned.txt','w');
for i = 1:length(seqs)
    fprintf(fileID,seqs(i).Sequence);
    fprintf(fileID,'\n');
end

fprintf(fileID,C);
fprintf(fileID,'\n');

end

