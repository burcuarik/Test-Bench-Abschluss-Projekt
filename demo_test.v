`timescale 1ns / 1ps

module tb_get_abs_pos_state_machine;
reg clk;
reg rst;
reg init_state_machine;
reg hls_done;
reg hls_ready;
reg [31:0] axis1_hw_counter;
reg [31:0] axis1_set_position_part1;
reg [31:0] axis1_set_position_part2;
reg [31:0] axis1_counts_per_m;
reg [31:0] axis2_hw_counter;
reg [31:0] axis2_set_position_part1;
reg [31:0] axis2_set_position_part2;
reg [31:0] axis2_counts_per_m;
reg [63:0] selected_axis_hls_calculated_abs_pos;
wire start_hls_calculations;
wire [2:0] state;
wire [31:0] selected_axis_hw_counter;
wire [31:0] selected_axis_set_position_part1;
wire [31:0] selected_axis_set_position_part2;
wire [31:0] selected_axis_counts_per_m;
wire [63:0] axis1_hls_calculated_abs_pos;
wire [63:0] axis2_hls_calculated_abs_pos;
get_abs_pos_state_machine ttb_get_abs_pos_state_machine
(
	.clk(clk),
	.rst(rst),
	.init_state_machine(init_state_machine),
	.hls_done(hls_done),
	.hls_ready(hls_ready),
	.axis1_hw_counter(axis1_hw_counter),
	.axis1_set_position_part1(axis1_set_position_part1),
	.axis1_set_position_part2(axis1_set_position_part2),
	.axis1_counts_per_m(axis1_counts_per_m),
	.axis2_hw_counter(axis2_hw_counter),
	.axis2_set_position_part1(axis2_set_position_part1),
	.axis2_set_position_part2(axis2_set_position_part2),
	.axis2_counts_per_m(axis2_counts_per_m),
	.selected_axis_hls_calculated_abs_pos(selected_axis_hls_calculated_abs_pos),
	.start_hls_calculations(start_hls_calculations),
	.state(state),
	.selected_axis_hw_counter(selected_axis_hw_counter),
	.selected_axis_set_position_part1(selected_axis_set_position_part1),
	.selected_axis_set_position_part2(selected_axis_set_position_part2),
	.selected_axis_counts_per_m(selected_axis_counts_per_m),
	.axis1_hls_calculated_abs_pos(axis1_hls_calculated_abs_pos),
	.axis2_hls_calculated_abs_pos(axis2_hls_calculated_abs_pos)
);

initial
begin
	clk = 0;
	rst = 1;
	init_state_machine = 0;
	hls_done = 0;
	hls_ready = 0;
	axis1_hw_counter = 0;
	axis1_set_position_part1 = 0;
	axis1_set_position_part2 = 0;
	axis1_counts_per_m = 0;
	axis2_hw_counter = 0;
	axis2_set_position_part1 = 0;
	axis2_set_position_part2 = 0;
	axis2_counts_per_m = 0;
	selected_axis_hls_calculated_abs_pos = 0;
	start_hls_calculations = 0;
	state = 0;
	selected_axis_hw_counter = 0;
	selected_axis_set_position_part1 = 0;
	selected_axis_set_position_part2 = 0;
	selected_axis_counts_per_m = 0;
	axis1_hls_calculated_abs_pos = 0;
	axis2_hls_calculated_abs_pos = 0;

	repeat (10) @(posedge clk);
	rst = 0;

	repeat (10) @(posedge clk);

	repeat (10) @(posedge clk);
	rst = 1;

end

always #20 clk <= ~clk;

endmodule

