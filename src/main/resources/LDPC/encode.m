function [ doubleenc ] = encode( data )

TxRx.Sim.name = 'MY_Gallager';
TxRx.Sim.SNR_dB_list = [0:1:8];
TxRx.Decoder.LDPC.Scheduling = 'Layered'; % 'Layered' and 'Flooding'
TxRx.Decoder.LDPC.Type = 'MPA'; % 'MPA' and 'SPA' (optimal)
TxRx.Decoder.LDPC.Iterations = 10;
LDPC = LDPC_11nD2_648b_R12();

blockSize = 144;
encodedData = [];

from = 1;
while from < length(data)
    if from + blockSize > length(data)
        padded = [data(from:length(data)) zeros(1, blockSize-(length(data)-from+1))];
        encodedData = [encodedData padded*LDPC.G];
    else
        %disp(size(data(from:from + blockSize)));
        encodedData = [encodedData data(from:from + blockSize-1)*LDPC.G];
    end
    from = from + blockSize;
end

doubleenc = round(double(encodedData.x));

end

