function decodedData = decode(encodedData)

%noise = randn(1,length(encodedData)); 
%encodedData = encodedData + noise*sqrt(0.3);

TxRx.Sim.name = 'MY_Gallager';
TxRx.Sim.SNR_dB_list = [0:1:8];
TxRx.Decoder.LDPC.Scheduling = 'Layered'; % 'Layered' and 'Flooding'
TxRx.Decoder.LDPC.Type = 'MPA'; % 'MPA' and 'SPA' (optimal)
TxRx.Decoder.LDPC.Iterations = 10;
LDPC = LDPC_11nD2_648b_R12();

blockSize = 288;
decodedData = [];

from = 1;
while from < length(encodedData)
    LLR_A2 = encodedData(from:from+blockSize-1);
    [decodedPortion,LLR_D2,NumC,NumV] = decLDPC_layered(TxRx,LDPC,LLR_A2);
    decodedData = [decodedData decodedPortion];
    from = from + blockSize;
end

%disp(encodedData);
%disp(decodedData);

end

