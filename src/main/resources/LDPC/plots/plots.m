% =========================================================================
% Title       : Generates an example plot
% File        : plots.m
% -------------------------------------------------------------------------
% Description :
%   See title!
% ------------------------------------------------------------------------- 
% Revisions   :
%   Date       Version  Author  Description
%   11-dec-11  1.1      studer  minor modifications
%   20-may-11  1.0      studer  created
% -------------------------------------------------------------------------
%   (C) 2006-2011 Communication Theory Group                      
%   ETH Zurich, 8092 Zurich, Switzerland                               
%   Author: Dr. Christoph Studer (e-mail: studer@rice.edu)     
% =========================================================================


% ------------------------------------------------
FileName = 'ERR_LDPC_648b_R12_LAYERED_SPA_I5';
Len = 10;
for idx=1:Len
 tmp = load([FileName,'_',num2str(idx-1),'.mat']);
 if idx==1
   BER = zeros(1,length(tmp.Results.BER));
   FER = zeros(1,length(tmp.Results.BER)); 
   SNRdB = tmp.Results.TxRx.Sim.SNR_dB_list;
 end
 BER = BER + tmp.Results.BER/Len;
 FER = FER + tmp.Results.FER/Len; 
end
semilogy(SNRdB,BER,'bo-'); hold on

% ------------------------------------------------
FileName = 'ERR_LDPC_648b_R12_LAYERED_MPA_I5';
Len = 10;
for idx=1:Len
 tmp = load([FileName,'_',num2str(idx-1),'.mat']);
 if idx==1
   BER = zeros(1,length(tmp.Results.BER));
   FER = zeros(1,length(tmp.Results.BER)); 
   SNRdB = tmp.Results.TxRx.Sim.SNR_dB_list;
 end
 BER = BER + tmp.Results.BER/Len;
 FER = FER + tmp.Results.FER/Len; 
end
semilogy(SNRdB,BER,'ro-'); hold on

xlabel('SNR [dB]')
ylabel('BER')

legend('SPA','MPA')

grid on
hold off

