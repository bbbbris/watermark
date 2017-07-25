function LDPC = MyGallager
%MYGALLAGER Summary of this function goes here
%Detailed explanation goes here


%Gallager_construction_LDPC();

H = getH();

%H = ParityMatrixTransformation(H);

spy(H)

LDPC.H = H;
LDPC.name = 'MY Gallager';


disp(LDPC);

LDPC = generateMyCodes(LDPC);


end

