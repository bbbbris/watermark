function LDPC = generateMyCodes(LDPC)

tic
disp(sprintf('\n*** %s\n',LDPC.name))

[LDPC.par_bits,LDPC.tot_bits] = size(LDPC.H);
LDPC.inf_bits = LDPC.tot_bits - LDPC.par_bits;
LDPC.rate = LDPC.inf_bits/LDPC.tot_bits;

% == compute parity check matrix
disp('Compute generator matrix G ...');
LDPC.H = gf(LDPC.H,1);
H1 = LDPC.H(:,1:LDPC.inf_bits);
H2 = LDPC.H(:,LDPC.inf_bits+1:end);
LDPC.P = (inv(H2)*(-H1))';

% -- construct parity check matrix
LDPC.G = [ gf(eye(LDPC.inf_bits),1) LDPC.P ];

% -- store LDPC construct
disp('Storing LDPC data to disc ...');
save(LDPC.name,'LDPC');

toc
return

% -- generate permutation matrix
    function PZ = eyeperm(Z,p)
        p = mod(p,Z);
        EZ = eye(Z);
        PZ = [ EZ(:,Z-p+1:Z) EZ(:,1:Z-p) ];
        return
        
        
        
    end

end
